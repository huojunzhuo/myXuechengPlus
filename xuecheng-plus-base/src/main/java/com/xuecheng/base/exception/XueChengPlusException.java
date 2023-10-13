package com.xuecheng.base.exception;

/**
 * ClassName: XueChengPlusException
 * Package: com.xuecheng.base.exception
 * Description:学成在线项目异常类
 *
 * @Author huojz
 * @Create 2023/10/12 21:08
 * @Version 1.0
 */
public class XueChengPlusException extends RuntimeException{
    private String errMessage;

    public XueChengPlusException() {
        super();
    }

    public XueChengPlusException(String errMessage) {
        super(errMessage);
        this.errMessage = errMessage;
    }

    public String getErrMessage() {
        return errMessage;
    }

    public static void cast(CommonError commonError){
        throw new XueChengPlusException(commonError.getErrMessage());
    }
    public static void cast(String errMessage){
        throw new XueChengPlusException(errMessage);
    }

}
