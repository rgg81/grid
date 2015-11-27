package com.gu.mediaservice.lib.auth

import akka.actor.Scheduler
import akka.agent.Agent
import com.amazonaws.auth.AWSCredentials
import com.amazonaws.services.s3.model.S3Object
import com.gu.mediaservice.lib.auth.PermissionType.PermissionType
import com.gu.mediaservice.lib.aws.S3
import com.gu.mediaservice.lib.config.Properties
import org.apache.commons.io.IOUtils
import org.slf4j.LoggerFactory
import play.api.libs.concurrent.Execution.Implicits._

import scala.collection.JavaConverters._
import scala.concurrent.Future
import scala.concurrent.duration._
import scala.util.{Success, Try}

abstract class BaseStore[TStoreKey, TStoreVal](bucket: String, credentials: AWSCredentials) {
  val s3 = new S3(credentials)

  private val log = LoggerFactory.getLogger(getClass)

  protected val store: Agent[Map[TStoreKey, TStoreVal]] = Agent(Map.empty)

  protected def getS3Object(key: String): Option[String] = {
    log.info(s"key: $key in bucket: $bucket")
    val content = Try[S3Object](s3.client.getObject(bucket, key))
    content match {
      case Success(cont) =>
        val stream = cont.getObjectContent
        val result = IOUtils.toString(stream, "utf-8").trim
        stream.close()
        Some(result)
      case _ => None
    }

  }

  def scheduleUpdates(scheduler: Scheduler) {
    scheduler.schedule(0.seconds, 10.minutes)(update())
  }

  def update(): Unit
}

class KeyStore(bucket: String, credentials: AWSCredentials) extends BaseStore[String, String](bucket, credentials) {
  def lookupIdentity(key: String): Future[Option[String]] =
    store.future.map(_.get(key))

  def findKey(prefix: String): Option[String] = s3.syncFindKey(bucket, prefix)

  def update() {
    store.sendOff(_ => fetchAll)
  }

  private def fetchAll: Map[String, String] = {
    val keys = s3.client.listObjects(bucket).getObjectSummaries.asScala.map(_.getKey)
    keys.flatMap(k => getS3Object(k).map(k -> _)).toMap
  }
}

object PermissionType extends Enumeration {
  type PermissionType = Value
  val EditMetadata = Value("editMetadata")
  val DeleteImage  = Value("deleteImage")
  val DeleteCrops  = Value("deleteCrops")
}

class PermissionStore(bucket: String, credentials: AWSCredentials) extends BaseStore[PermissionType, List[String]](bucket, credentials) {
  def hasPermission(permission: PermissionType, userEmail: String) = {
    store.future().map {
      list => {
        list.get(permission) match {
          case Some(userList) => userList.contains(userEmail.toLowerCase)
          case None => false
        }
      }
    }
  }

  def update() {
    store.sendOff(_ => getList())
  }

  private def getList(): Map[PermissionType, List[String]] = {
    val fileContents = getS3Object("permissions.properties")
    fileContents match {
      case Some(contents) => {
        val properties = Properties.fromString(contents)

        PermissionType.values.toList.map(permission => {
          properties.get(permission.toString) match {
            case Some(value) => (permission, value.split(",").toList.map(_.toLowerCase))
            case None => (permission, List())
          }
        }).toMap
      }
      case None => {
        PermissionType.values.toList.map(permission => (permission, List())).toMap
      }
    }
  }
}
