package com.gu.mediaservice.lib.config

class Services(domainRoot: String, ssl: Boolean) {
  // FIXME: do this via new config properties!
  val DomainExtractor = """^([-a-z]+)\.(.*)""".r
  val (appName, parentDomain) = domainRoot match {
    case DomainExtractor(a, b) => (a, b)
  }

  val kahunaHost: String   = domainRoot
  val apiHost: String      = s"api-$domainRoot"
  val loaderHost: String   = s"loader-$domainRoot"
  val cropperHost: String  = s"cropper-$domainRoot"
  val metadataHost: String = s"metadata-$domainRoot"
  val imgopsHost: String = s"imgops-$domainRoot"
  val usageHost: String = s"usage-$domainRoot"
  val collectionsHost: String = s"collections-$domainRoot"


  val kahunaBaseUri      = baseUri(kahunaHost)
  val apiBaseUri         = baseUri(apiHost)
  val loaderBaseUri      = baseUri(loaderHost)
  val cropperBaseUri     = baseUri(cropperHost)
  val metadataBaseUri    = baseUri(metadataHost)
  val imgopsBaseUri      = baseUri(imgopsHost)
  val usageBaseUri       = baseUri(usageHost)
  val collectionsBaseUri = baseUri(collectionsHost)


  val loginUriTemplate = s"$kahunaBaseUri/login{?redirectUri}"

  def baseUri(host: String) = {
    val protocol = if (ssl) "https" else "http"
    s"$protocol://$host"
  }
}
