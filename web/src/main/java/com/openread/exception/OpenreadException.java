/*
 * Copyright 2013 Aliyun.com All right reserved. This software is the
 * confidential and proprietary information of Aliyun.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Aliyun.com .
 */
package com.openread.exception;

/**
 * @author <a href="mailto:li.jinl@alibaba-inc.com">Stone.J</a> Nov 19, 2013
 */
public class OpenreadException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * MT Auto Exception Code.
     * 
     * @author <a href="mailto:li.jinl@alibaba-inc.com">Stone.J</a> Nov 19, 2013
     */
    public static enum MtautoCode {
        ADB_NOT_EXISTED,
        ADB_FAILED,
        ADB_TIMEOUT,
        OVER_MAX_INSTANCE
    }

    private MtautoCode code;

    public OpenreadException(MtautoCode code) {
        this(code, null, null);
    }

    public OpenreadException(MtautoCode code, Throwable cause) {
        this(code, null, cause);
    }

    public OpenreadException(MtautoCode code, String message) {
        this(code, message, null);
    }

    public OpenreadException(MtautoCode code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    public MtautoCode getCode() {
        return code;
    }

}
