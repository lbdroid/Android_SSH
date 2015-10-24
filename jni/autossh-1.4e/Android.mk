###################### autossh ######################

LOCAL_PATH:= $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE_TAGS := optional

LOCAL_SRC_FILES := autossh.c

LOCAL_MODULE := autossh

LOCAL_CFLAGS := -DVER=\"1.4e\" -DSSH_PATH=\"/data/bin/ssh\"

LOCAL_SHARED_LIBRARIES += libssh libssl libcrypto libdl
LOCAL_LDLIBS := -llog -lz

include $(BUILD_EXECUTABLE)
