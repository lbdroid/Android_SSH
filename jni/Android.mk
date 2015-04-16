LOCAL_PATH:= $(call my-dir)
OLD_PATH:=$(call my-dir)
include $(CLEAR_VARS)
include $(LOCAL_PATH)/external/openssl/Android.mk
LOCAL_PATH:=$(OLD_PATH)
include $(CLEAR_VARS)
include $(LOCAL_PATH)/external/zlib/Android.mk
LOCAL_PATH:=$(OLD_PATH)
###################### libssh ######################
include $(CLEAR_VARS)

LOCAL_MODULE_TAGS := optional

LOCAL_SRC_FILES := \
    authfd.c authfile.c bufaux.c bufbn.c buffer.c \
    canohost.c channels.c cipher.c cipher-aes.c \
    cipher-bf1.c cipher-ctr.c cipher-3des1.c cleanup.c \
    compat.c compress.c crc32.c deattack.c fatal.c hostfile.c \
    log.c match.c md-sha256.c moduli.c nchan.c packet.c roaming_common.c \
    roaming_serv.c readpass.c rsa.c ttymodes.c xmalloc.c addrmatch.c \
    atomicio.c key.c dispatch.c kex.c mac.c uidswap.c uuencode.c misc.c \
    monitor_fdpass.c rijndael.c ssh-dss.c ssh-ecdsa.c ssh-rsa.c dh.c \
    kexdh.c kexgex.c kexdhc.c kexgexc.c bufec.c kexecdh.c kexecdhc.c \
    msg.c progressmeter.c dns.c entropy.c gss-genr.c umac.c umac128.c \
    jpake.c schnorr.c ssh-pkcs11.c krl.c \
    openbsd-compat/strtonum.c openbsd-compat/bsd-misc.c \
    openbsd-compat/timingsafe_bcmp.c openbsd-compat/bsd-getpeereid.c \
    openbsd-compat/readpassphrase.c openbsd-compat/vis.c \
    openbsd-compat/port-tun.c openbsd-compat/setproctitle.c \
    openbsd-compat/bsd-closefrom.c  openbsd-compat/getopt_long.c \
    openbsd-compat/rresvport.c openbsd-compat/bindresvport.c \
    openbsd-compat/bsd-statvfs.c openbsd-compat/xmmap.c \
    openbsd-compat/port-linux.c openbsd-compat/strmode.c \
    openbsd-compat/bsd-openpty.c \
    openbsd-compat/fmt_scaled.c \
    openbsd-compat/pwcache.c openbsd-compat/glob.c

#    openbsd-compat/getrrsetbyname.c
#    openbsd-compat/xcrypt.c 

LOCAL_C_INCLUDES := $(LOCAL_PATH)/external/openssl/include external/zlib $(LOCAL_PATH)/include
PRIVATE_C_INCLUDES := $(LOCAL_PATH)/external/openssl/openbsd-compat $(LOCAL_PATH)/include

LOCAL_SHARED_LIBRARIES += libssl libcrypto libdl libz

LOCAL_MODULE := libssh

LOCAL_CFLAGS+=-O3

include $(BUILD_SHARED_LIBRARY)

###################### ssh ######################

include $(CLEAR_VARS)

LOCAL_MODULE_TAGS := optional

LOCAL_SRC_FILES := \
    ssh.c readconf.c clientloop.c sshtty.c \
    sshconnect.c sshconnect1.c sshconnect2.c mux.c \
    roaming_common.c roaming_client.c

LOCAL_MODULE := _ssh

LOCAL_C_INCLUDES := $(LOCAL_PATH)/external/openssl/include $(LOCAL_PATH)/include
PRIVATE_C_INCLUDES := $(LOCAL_PATH)/external/openssl/openbsd-compat $(LOCAL_PATH)/include

LOCAL_SHARED_LIBRARIES += libssh libssl libcrypto libdl libz

include $(BUILD_EXECUTABLE)

###################### sftp ######################

include $(CLEAR_VARS)

LOCAL_MODULE_TAGS := optional

LOCAL_SRC_FILES := \
    sftp.c sftp-client.c sftp-common.c sftp-glob.c progressmeter.c

LOCAL_MODULE := sftp

LOCAL_C_INCLUDES := $(LOCAL_PATH)/external/openssl/include $(LOCAL_PATH)/include
PRIVATE_C_INCLUDES := $(LOCAL_PATH)/external/openssl/openbsd-compat $(LOCAL_PATH)/include

LOCAL_SHARED_LIBRARIES += libssh libssl libcrypto libdl libz

include $(BUILD_EXECUTABLE)

###################### scp ######################

include $(CLEAR_VARS)

LOCAL_MODULE_TAGS := optional

LOCAL_SRC_FILES := \
    scp.c progressmeter.c bufaux.c

LOCAL_MODULE := scp

LOCAL_C_INCLUDES := $(LOCAL_PATH)/external/openssl/include $(LOCAL_PATH)/include
PRIVATE_C_INCLUDES := $(LOCAL_PATH)/external/openssl/openbsd-compat $(LOCAL_PATH)/include

LOCAL_SHARED_LIBRARIES += libssh libssl libcrypto libdl libz

include $(BUILD_EXECUTABLE)

###################### sshd ######################

include $(CLEAR_VARS)

LOCAL_MODULE_TAGS := optional

LOCAL_SRC_FILES := \
	sshd.c auth-rhosts.c auth-rsa.c auth-rh-rsa.c \
	audit.c audit-bsm.c audit-linux.c platform.c \
	sshpty.c sshlogin.c servconf.c serverloop.c \
	auth.c auth1.c auth2.c auth-options.c session.c \
	auth-chall.c auth2-chall.c groupaccess.c \
	auth-skey.c auth-bsdauth.c auth2-hostbased.c auth2-kbdint.c \
	auth2-none.c auth2-passwd.c auth2-pubkey.c auth2-jpake.c \
	monitor_mm.c monitor.c monitor_wrap.c kexdhs.c kexgexs.c kexecdhs.c \
	auth-krb5.c \
	auth2-gss.c gss-serv.c gss-serv-krb5.c \
	loginrec.c auth-pam.c auth-shadow.c auth-sia.c md5crypt.c \
	sftp-server.c sftp-common.c \
	roaming_common.c roaming_serv.c \
	sandbox-null.c sandbox-rlimit.c sandbox-systrace.c \
	sandbox-seccomp-filter.c

# auth-passwd.c

LOCAL_MODULE := sshd

LOCAL_C_INCLUDES := $(LOCAL_PATH)/external/openssl/include external/zlib $(LOCAL_PATH)/include
PRIVATE_C_INCLUDES := $(LOCAL_PATH)/external/openssl/openbsd-compat $(LOCAL_PATH)/include

LOCAL_SHARED_LIBRARIES += libssh libssl libcrypto libdl libz

include $(BUILD_EXECUTABLE)

###################### sftp-server ######################

include $(CLEAR_VARS)

LOCAL_MODULE_TAGS := optional

LOCAL_SRC_FILES := \
       sftp-server.c sftp-common.c sftp-server-main.c

LOCAL_MODULE := sftp-server

LOCAL_C_INCLUDES := $(LOCAL_PATH)/external/openssl/include $(LOCAL_PATH)/include
PRIVATE_C_INCLUDES := $(LOCAL_PATH)/external/openssl/openbsd-compat $(LOCAL_PATH)/include

LOCAL_SHARED_LIBRARIES += libssh libssl libcrypto libdl libz

include $(BUILD_EXECUTABLE)

###################### ssh-keygen ######################

include $(CLEAR_VARS)

LOCAL_MODULE_TAGS := optional

LOCAL_SRC_FILES := \
    ssh-keygen.c

LOCAL_MODULE := ssh-keygen

LOCAL_C_INCLUDES := $(LOCAL_PATH)/external/openssl/include $(LOCAL_PATH)/include
PRIVATE_C_INCLUDES := $(LOCAL_PATH)/external/openssl/openbsd-compat $(LOCAL_PATH)/include

LOCAL_SHARED_LIBRARIES += libssh libssl libcrypto libdl libz

include $(BUILD_EXECUTABLE)

###################### voodoo ######################

all:
	rm -rf $(OLD_PATH)/../assets/*
	mkdir -p $(OLD_PATH)/../assets/bin
	mkdir $(OLD_PATH)/../assets/etc
	cp $(OLD_PATH)/sshd_config.android $(OLD_PATH)/../assets/etc/sshd_config
	cp $(OLD_PATH)/start-ssh $(OLD_PATH)/../assets/bin/
	cp $(OLD_PATH)/../libs/armeabi/gzip $(OLD_PATH)/../assets/bin/
	cp $(OLD_PATH)/../libs/armeabi/scp $(OLD_PATH)/../assets/bin/
	cp $(OLD_PATH)/../libs/armeabi/sftp $(OLD_PATH)/../assets/bin/
	cp $(OLD_PATH)/../libs/armeabi/sftp-server $(OLD_PATH)/../assets/bin/
	cp $(OLD_PATH)/../libs/armeabi/_ssh $(OLD_PATH)/../assets/bin/ssh
	cp $(OLD_PATH)/../libs/armeabi/sshd $(OLD_PATH)/../assets/bin/
	cp $(OLD_PATH)/../libs/armeabi/ssh-keygen $(OLD_PATH)/../assets/bin/
	cp $(OLD_PATH)/../libs/armeabi/openssl $(OLD_PATH)/../assets/bin/