프로그래머스, 라인플러스 - 앱개발 챌린지
==
프로젝트 설명
--
- 사진 첨부가 가능한 메모장 제작

완성 모습
--

![KakaoTalk_20200224_060746507_01](https://user-images.githubusercontent.com/13197242/75120252-894c8800-56cd-11ea-95d5-46c490492c34.jpg)
![KakaoTalk_20200224_060746507_02](https://user-images.githubusercontent.com/13197242/75120254-8a7db500-56cd-11ea-8ad6-a77427250057.jpg)
![KakaoTalk_20200224_060746507_03](https://user-images.githubusercontent.com/13197242/75120255-8a7db500-56cd-11ea-8a37-3e595b0fc0d2.jpg)

기능구현
--
- 제목과 내용을 지정하여 메모를 저장가능
- 메모는 카메라, 갤러리, 인터넷 링크 첨부가능
- 메모데이터는 Room라이브러리를 사용하여 기기 내부에 저장
- MVP패턴을 적용 

구조
--
![캡처](https://user-images.githubusercontent.com/13197242/75120278-bbf68080-56cd-11ea-9baa-5d7f5b0b80db.PNG)
- ui.addedit : 메모 추가, 수정, 삭제기능 Activity
- ui.memo : 메모장 메인 activity
- database : Room db 설정로직과 메모와 이미지 저장을위한 쿼리문
- utils : rxjava용 라이프사이클관리 로직과, 사진추가 및 삭제를위한 유틸
