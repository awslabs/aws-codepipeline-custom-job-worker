#! /bin/sh
chkconfig aws-codepipeline-jobworker on
/etc/init.d/aws-codepipeline-jobworker start "com.amazonaws.codepipeline.jobworker.configuration.CustomActionJobWorkerConfiguration"
