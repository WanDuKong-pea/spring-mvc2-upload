package hello.upload.domain;

import lombok.Data;

/**
 * 업로드한 파일 정보를 보관할 객체
 */
@Data
public class UploadFile {
    //고객이 업로드한 파일명
    private String uploadFileName;
    //서버 내부에서 관리하는 파일명
    private String storeFileName;

    public UploadFile(String uploadFileName, String storeFileName){
        this.uploadFileName=uploadFileName;
        this.storeFileName=storeFileName;
    }

    //고객이 업로드한 파일명으로 서버 내부에 파일 저장을 하면 안됨
    //서로 다른 고객이 같은 파일 이름을 업로드 하는 경우 기존 파일 이름과 충돌 가능성
    //서버에서는 저장할 파일명이 겹치지 않도록 내부에서 관리하는 별도의 파일명이 필요
    //고객 아이디(유일)+파일명을 사용하기도 함
}
