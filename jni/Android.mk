LOCAL_PATH := $(call my-dir)
my_LOCAL_PATH := $(call my-dir)
OLD_PATH := $(call my-dir)

include $(CLEAR_VARS)
include $(my_LOCAL_PATH)/openssh/Android.mk
include $(my_LOCAL_PATH)/openssl/Android.mk
include $(my_LOCAL_PATH)/autossh-1.4e/Android.mk

###################### voodoo ######################
all:
	rm -rf $(OLD_PATH)/../assets/*
	mkdir -p $(OLD_PATH)/../assets/bin
	mkdir $(OLD_PATH)/../assets/etc
	cp $(OLD_PATH)/openssh/sshd_config.android $(OLD_PATH)/../assets/etc/sshd_config
	cp $(OLD_PATH)/openssh/start-ssh $(OLD_PATH)/../assets/bin/
	#cp $(OLD_PATH)/../libs/armeabi/gzip $(OLD_PATH)/../assets/bin/
	cp $(OLD_PATH)/../libs/armeabi-v7a/scp $(OLD_PATH)/../assets/bin/
	cp $(OLD_PATH)/../libs/armeabi-v7a/sftp $(OLD_PATH)/../assets/bin/
	cp $(OLD_PATH)/../libs/armeabi-v7a/sftp-server $(OLD_PATH)/../assets/bin/
	cp $(OLD_PATH)/../libs/armeabi-v7a/_ssh $(OLD_PATH)/../assets/bin/ssh
	cp $(OLD_PATH)/../libs/armeabi-v7a/sshd $(OLD_PATH)/../assets/bin/
	cp $(OLD_PATH)/../libs/armeabi-v7a/ssh-keygen $(OLD_PATH)/../assets/bin/
	cp $(OLD_PATH)/../libs/armeabi-v7a/openssl $(OLD_PATH)/../assets/bin/
	cp $(OLD_PATH)/../libs/armeabi-v7a/autossh $(OLD_PATH)/../assets/bin/