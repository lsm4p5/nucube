package uplus.nucube.common.argumentresolver;

import java.util.ArrayList;

public class UserEnc {

        private static ArrayList<String[]> UserData = new ArrayList<>();


        // 유저 생성
        public void set_USER(String ID, String Hasing_Password, String SALT) {
            String[] temp = {ID, Hasing_Password, SALT};
            UserData.add(temp);
        }

        // 들어온 ID 와 비밀번호가 일치하는지 체크
        public boolean check(String ID, String Hasing_password) {
            for(int i = 0; i < UserData.size(); i++) {
                if(ID.equals(UserData.get(i)[0])) {				// ID 일치하는 것을 찾을경우
                    if(Hasing_password.equals(UserData.get(i)[1])) {	// 다이제스트도 일치할 경우 true
                        return true;
                    }
                }
            }
            return false;
        }

        // 해당 ID 의 SALT 값 찾기
        public String get_SALT(String ID) {
            String err = null;		// 아이디가 존재하지 않을 경우 null 리턴
            for(int i = 0; i < UserData.size(); i++) {
                if(ID.equals(UserData.get(i)[0])) {
                    return UserData.get(i)[2];
                }
            }
            return err;
        }


        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            for(String[] temp : UserData) {
                sb.append("ID : " +temp[0] + "\tPassword : " + temp[1] + "\tSALT : "+ temp[2]).append("\n\n");
            }
            return sb.toString();
        }

}
