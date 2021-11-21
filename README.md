<h1>HttpFlow란</h1>
HttpFlow는 스크립트 형태로 http request를 요청하고 결과를 다른 요청에서 재사용하여<br/>
상호 연관된 2개 이상의 http request를 flow로 정의하여 실행할 수 있는 도구입니다.<br/>
<br/>
아래 예제와 같이 http protocol로 작성된 텍스트를 .hfd 파일로 저장하여 실행할 수 있습니다.<br/>

[ searchCurl.hfd ]
<pre>
:authority: www.google.com
:method: GET
:path: /search?q=curl&oq=curl&sourceid=chrome&ie=UTF-8
:scheme: https
</pre>
(위 형식은 크롬 브라우저의 개발자도구 > Network > Headers > Request Headers에 표시되는 형식으로<br/>
 별도로 편집할 필요 없이 그대로 복사하여 사용할 수 있습니다.)<br/>

OR

<pre>
GET https://www.google.com/search?q=curl&oq=curl&sourceid=chrome&ie=UTF-8 HTTP/1.0
</pre>

[실행방법]
<pre>
httpflow.sh searchCurl.hfd
</pre>
<br/><br/>
<h1>2개이상의 request에 flow 적용하기</h1>
2개 이상의 request는 element 구분자 키워드(---)를 이용하여 하나의 .hfd 파일에 작성할 수 있으며,<br/>
이 경우 첫번째 request의 응답으로 내려온 header, cookie, body의 값을 추출하여<br/>
두번째 request에서 사용하여 서버로 요청시 전달할 수 있습니다.<br/>
<br/>
* 아래 시나리오는 주문담당 개발자가 테스트로 주문을 1개 생성하고, 새로 생성된 주문번호로 배송처리하는<br/>
  2가지 API요청을 flow로 작성하여 자동화해본 가상의 예제입니다.<br/>
<br/>
[orderShippingTest.hfd]
<pre>
:authority: www.테스트할 도메인.com
:method: POST
:path: /orders
:scheme: https
Content-Type: application/json

{"productId" : "P04433", ... 주문 데이터 json}

\---

:authority: www.테스트할 도메인.com
:method: POST
:path: /orders/shipping
:scheme: https
Content-Type: application/json

{"orderId" : "${orderId}", ... 배송정보 json}</pre>
<br/>
두번째 request의 body데이터에 사용된 ${orderId} 부분이 첫번째 request의 응답데이터 body에 포함되어 내려온<br/>
<pre>
200 OK HTTP/1.0
... response headers ...

{"orderId" : "ORDER-000000001", ...}
</pre>
orderId값으로 치환되어, 최종적으로 아래와 같이 두번째 request의 body가 서버로 전송됩니다.<br/>
<pre>
:authority: www.테스트할 도메인.com
:method: POST
:path: /orders/shipping
:scheme: https
Content-Type: application/json

{"orderId" : "ORDER-000000001", ... 배송정보 json}</pre>
<br/>
<br/>
<h1>기타 활용팁</h1>
<pre>
개발자 : 기능개발 도중에 필요한 테스트 데이터를 생성하기 위해서 고객용 주문화면과 판매자용 어드민화면을
각각 접속하여 필수값을 입력하고 필요한 처리 버튼들을 직접 클릭하는 대신
위와 같이 작성된 httpflow파일을 실행함으로써 원하는 테스트 데이터를 한번에 생성 가능
</pre>

<pre>
QA담당자 : 테스트용 대량 데이터(페이징 개수를 초과하는 케이스 등)가 필요한 경우 아래와 같이 반복문의 형태로
동일한 httpflow 파일을 실행하여 대량 데이터 생성 가능

FOR 1000 HTTPFLOW/1.0

/---
:authority: www.테스트할 도메인.com
:method: POST
:path: /orders
:scheme: https
Content-Type: application/json

{"productId" : "P04433", ... 주문 데이터 json}
/---

END-FOR
</pre>

<pre>
IDE(현재는 IntelliJ 플러그인만 제공)에 httpflow 플러그인을 설치하여, 
화면 전환없이 IDE상에서 테스트 데이터 생성 가능
https://github.com/soungminjoo/httpflow-intellij-plugin
</pre>

<pre>
Jenkins 플러그인으로 httpflow를 설치하여,
주기적으로 특정 http request를 요청하도록 하고 서비스 정상 여부를 체크
https://github.com/soungminjoo/httpflow-jenkins-plugin
</pre>
