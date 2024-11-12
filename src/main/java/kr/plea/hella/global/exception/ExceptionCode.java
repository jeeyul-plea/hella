package kr.plea.hella.global.exception;

import lombok.Getter;

@Getter
public enum ExceptionCode {
    /*
     * # 400 BAD REQUEST
     *
     */
    INVALID_INPUT_VALUE(400, "잘못된 입력값입니다."),
    INVALID_TYPE_VALUE(400, "잘못된 타입 정보입니다."),
    INVALID_CREDENTIAL_VALUE(400, "잘못된 인증 정보입니다."),
    INVALID_ACCESS_TOKEN(400, "잘못된 토큰입니다."),
    INVALID_REFRESH_TOKEN(400, "잘못된 리프레쉬 토큰입니다."),
    EXPIRED_TOKEN(400, "만료된 토큰입니다."),
    NOT_EXPIRED_TOKEN_YET(400, "만료되지 않은 토큰입니다."),
    NOT_CHANGED_PASSWORD(400, "최근 사용한 비밀번호입니다. 다른 비밀번호를 선택해 주세요."),
    MISS_MATCH_PASSWORD(400, "비밀번호가 일치하지 않습니다."),
    MISS_MATCH_USERID(400,"유저 아이디가 일치하지 않습니다."),
    NOT_CHANGED_NICKNAME(400, "최근 사용한 닉네임입니다. 다른 닉네임을 선택해 주세요."),
    POST_ALREADY_LIKED(400, "이미 게시글에 좋아요를 눌렀습니다."),
    COMMENT_ALREADY_LIKED(400, "이미 댓글에 좋아요를 눌렀습니다."),
    COMMENT_DELETED(400, "삭제된 댓글입니다."),

    /*
     * # 401 UNAUTHORIZED
     *
     */
    UNAUTHORIZED(401, "권한이 없습니다."),

    /*
     * # 403 ACCESS_DENIED
     *
     */
    ACCESS_DENIED(403, "접근이 거부되었습니다."),

    /*
     * # 404 NOT_FOUND
     *
     */
    USER_NOT_FOUND(404, "등록되지 않은 사용자입니다."),
    USER_WITHDRAW(404, "탈퇴한 사용자입니다."),
    USER_INACTIVE(404, "휴면 계정으로 전환된 사용자입니다."),
    POST_NOT_FOUND(404, "게시글이 존재하지 않습니다."),
    COMMENT_NOT_FOUND(404, "댓글이 존재하지 않습니다."),
    POST_LIKE_NOT_FOUND(404,"게시글 좋아요가 존재하지 않습니다."),

    /*
     * # 405 METHOD NOT ALLOWED
     *
     */

    /*
     * # 406 NOT_ACCEPTABLE
     *
     */
    NOT_ACCEPTABLE(406, "잘못된 전송 포맷입니다."),

    /*
     * # 409 CONFLICT
     *
     */
    ALREADY_EXISTS_ID(409,"이미 등록된 'ID' 정보가 존재합니다."),

    /*
     * # 500 Internal server error
     *
     */
    INTERNAL_SERVER_ERROR(500, "Internal server error"),
    NOTIFICATION_CONNECTION_ERROR(500, "알림 연결에 실패했습니다."),
    CONVERT_TO_JSON_ERROR(500, "JSON 변환에 실패했습니다.")
    ;

    private final int status;
    private final String message;

    ExceptionCode(int status, String message) {
        this.status = status;
        this.message = message;
    }
}