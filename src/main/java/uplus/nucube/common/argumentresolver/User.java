package uplus.nucube.common.argumentresolver;

import java.security.MessageDigest;
import java.security.SecureRandom;

public class User {

        private static final int SALT_SIZE = 16;
        private static UserEnc db = new UserEnc();


        // 새로운 계정 만들기
        public void set_User(String ID, byte[] Password) throws Exception {
            String SALT = getSALT();
            db.set_USER(ID, Hashing(Password, SALT), SALT);
        }


        // 유저 정보와 대조한 뒤 로그인 하기
        public void get_User(String ID, byte[] password) throws Exception {
            String temp_salt = db.get_SALT(ID);					// 해당 ID의 SALT 값을 찾는다

            String temp_pass = Hashing(password, temp_salt);	// 얻어온 Salt 와 password 를 조합해본다.

            if(db.check(ID,temp_pass)) {						// db 에 저장된 아이디와 비밀번호를 대조한다
                System.out.println("로그인 성공");
            }
            else {
                System.out.println("로그인 실패");
            }

        }


        // 비밀번호 해싱
        private String Hashing(byte[] password, String Salt) throws Exception {

            MessageDigest md = MessageDigest.getInstance("SHA-256");	// SHA-256 해시함수를 사용

            // key-stretching
            for(int i = 0; i < 10000; i++) {
                String temp = Byte_to_String(password) + Salt;	// 패스워드와 Salt 를 합쳐 새로운 문자열 생성
                md.update(temp.getBytes());						// temp 의 문자열을 해싱하여 md 에 저장해둔다
                password = md.digest();							// md 객체의 다이제스트를 얻어 password 를 갱신한다
            }

            return Byte_to_String(password);
        }


        // SALT 값 생성
        private String getSALT() throws Exception {
            SecureRandom rnd = new SecureRandom();
            byte[] temp = new byte[SALT_SIZE];
            rnd.nextBytes(temp);

            return Byte_to_String(temp);

        }


        // 바이트 값을 16진수로 변경해준다
        private String Byte_to_String(byte[] temp) {
            StringBuilder sb = new StringBuilder();
            for(byte a : temp) {
                sb.append(String.format("%02x", a));
            }
            return sb.toString();
        }

        public void get_DB() {
            System.out.println("\n================DB출력================\n");
            System.out.println(db);
        }

}
