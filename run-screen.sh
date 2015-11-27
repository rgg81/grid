#!/bin/bash

screen -S elasticsearch -d -m ./elasticsearch/dev-start.sh
screen -S media-api -d -m sbt 'project media-api'    'run 9001'
screen -S thrall -d -m sbt 'project thrall'       'run 9002'
screen -S image-loader -d -m sbt 'project image-loader' 'run 9003'
screen -S ftp-watcher -d -m sbt 'project ftp-watcher'  'run 9004'
screen -S kahuna -d -m sbt 'project kahuna'       'run 9005'
screen -S cropper -d -m sbt 'project cropper'      'run 9006'
screen -S usage -d -m sbt 'project usage'        'run 9009'
screen -S metada-editor -d -m sbt 'project metadata-editor' 'run 9007'
screen -S imgops -d -m ./imgops/dev-start.sh
