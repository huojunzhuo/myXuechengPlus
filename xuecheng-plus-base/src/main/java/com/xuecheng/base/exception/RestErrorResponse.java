package com.xuecheng.base.exception;

/**
 * ClassName: RestErrorResponse
 * Package: com.xuecheng.base.exception
 * Description:错误响应参数包装
 *
 * @Author huojz
 * @Create 2023/10/12 21:10
 * @Version 1.0
 */

import java.io.Serializable;

/**
 * 错误响应参数包装
 */

public class RestErrorResponse implements Serializable {
    private String errMessage;

    public RestErrorResponse(String errMessage){
        this.errMessage= errMessage;
    }

    public String getErrMessage() {
        return errMessage;
    }

    public void setErrMessage(String errMessage) {
        this.errMessage = errMessage;
    }

}
