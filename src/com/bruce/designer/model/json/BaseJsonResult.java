package com.bruce.designer.model.json;


/**
 * 对数据的json包装，ajax请求及mcs共用此数据类型
 * 
 * @author liqian
 * 
 */
public class BaseJsonResult{

    private int result;
    
    private int errorcode;

    private String message;

    public BaseJsonResult() {
        super();
    }
    
    public BaseJsonResult(int result, int errorcode, String message){
        this.result = result;
        this.errorcode = errorcode;
        this.message = message;
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

	public int getErrorcode() {
		return errorcode;
	}

	public void setErrorcode(int errorcode) {
		this.errorcode = errorcode;
	}
    
}