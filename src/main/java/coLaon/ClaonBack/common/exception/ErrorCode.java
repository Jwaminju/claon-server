package coLaon.ClaonBack.common.exception;

public enum ErrorCode {

    /**
     * 400 Bad Request
     */
    ROW_ALREADY_DELETED(40000),
    ROW_ALREADY_EXIST(40001),
    WRONG_ADDRESS(40002),
    INVALID_FORMAT(40003),
    /**
     *  401 Unauthorized Error
     */
    NOT_ACCESSIBLE(40100);

    private final int code;

    ErrorCode(int code) {
        this.code = code;
    }

    public int getCode() {
        return this.code;
    }
}