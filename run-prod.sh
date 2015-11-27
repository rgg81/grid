#!/bin/bash


screen -S elasticsearch -d -m ./elasticsearch/dev-start.sh
sbt 'project media-api' 'clean' 'stage'
sbt 'project thrall' 'clean' 'stage'
sbt 'project image-loader' 'clean' 'stage'
#sbt 'project ftp-watcher' 'clean' 'stage'
sbt 'project kahuna' 'clean' 'stage'
sbt 'project cropper' 'clean' 'stage'
cp cropper/srgb.icc cropper/target/universal/stage/
#sbt 'project usage' 'clean' 'stage'
sbt 'project metadata-editor' 'clean' 'stage'
./media-api/target/universal/stage/bin/media-api -Dapplication.secret="0nhe2L3l7gLkWoWdJtTnNgb^w9jt6SL2I9OYz2Fr4q_4ET2;xNIoikGqYk]/PE[" -Dhttp.port="9001" > media-api.log &
./thrall/target/universal/stage/bin/thrall -Dapplication.secret="<vS_=U]M7<lWoUE;NbKlnjc9O4/MNPa@OV1Qm]N<UWj4Un;nBQttAUqFZ_I3ZAyC" -Dhttp.port="9002" > thrall.log &
./image-loader/target/universal/stage/bin/image-loader -Dapplication.secret="LO_ASa_zzoR2Hi4iLi3wxhA6TE2>9HG>;2?@bsiqU;]IT2cWcmIAcxFQFy1dU?Vn" -Dhttp.port="9003" > loader.log &
#./ftp-loader/target/universal/stage/bin/ftp-loader -Dhttp.port=9004 > ftp-watcher.log &
./kahuna/target/universal/stage/bin/kahuna -Dapplication.secret="nYVuEvJ8Vq6=Wm>jlLsI?;AzNT6Pjq4343sj8RkG1UnTO=I<UEE;m=e;SBy48UYE" -Dhttp.port="9005" > kahuna.log &
./cropper/target/universal/stage/bin/cropper -Dapplication.secret=":?dauErldXis1mb7==FfFU18W6CUNc0_q=ZpvOvvox5K_;gKIFT9;ZrYuWwZx<qc" -Dhttp.port="9006" > cropper.log &
#./usage/target/universal/stage/bin/usage -Dhttp.port=9009 > usage.log &
./metadata-editor/target/universal/stage/bin/metadata-editor -Dapplication.secret="Et0VXIi_yL9_pWadCfywe9nMn/g@QPv@tGYJrl6X]5?XFGICni21nV[z@Wz]RJPh" -Dhttp.port="9007" > metadata-editor.log &
./imgops/dev-start.sh


