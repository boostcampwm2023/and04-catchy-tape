# 📼 Catchy Tape
<p align="center">
<img width="220" src="https://github.com/boostcampwm2023/and04-catchy-tape/assets/62279741/155828a9-a9b3-4cdf-acc5-95efed03e2bf"/>
</p>
<div align="center">
    
누구든지 노래를 공유할 수 있는  
숨겨진 아티스트의 노래를 들을 수 있는  
**Catchy Tape** 로 오세요 ~ 📼 📼 


[![Hits](https://hits.seeyoufarm.com/api/count/incr/badge.svg?url=https%3A%2F%2Fgithub.com%2Fboostcampwm2023%2Fand04-catchy-tape&count_bg=%23BB2649&title_bg=%23555555&icon=&icon_color=%23BB2649&title=hits&edge_flat=false)](https://hits.seeyoufarm.com)
</div>

<div align="center">
    <a href="https://github.com/boostcampwm2023/and04-catchy-tape/wiki" target="_blank">🍗오도독 Wiki</a> &nbsp 
    <a href="https://github.com/orgs/boostcampwm2023/projects/39" target="_blank">🗓 프로젝트 일정 관리</a> 
</div>

## OverView
| Player  | Playlist | Upload | Search |
| ------------- | ------------- | ------------- | ------------- |
| <img width="200" src="https://github.com/boostcampwm2023/and04-catchy-tape/assets/62279741/a769d2f0-f3e1-4af4-8c97-58da38230ee7" /> | <img width="200" src="https://github.com/boostcampwm2023/and04-catchy-tape/assets/62279741/0789dab5-644d-4709-95c8-5dd4d16a5094"/> | <img width="200" src="https://github.com/boostcampwm2023/and04-catchy-tape/assets/62279741/c6443860-136d-444d-908e-94162714d81d" /> | <img width="200" src="https://github.com/boostcampwm2023/and04-catchy-tape/assets/62279741/956609bb-cfbb-4cf2-b676-89afa01e1837" />


## TechStack
### 🤖 Android
| Category  | TechStack | 기록 |
| ------------- | ------------- | ------------- |
| Architecture  | Clean Architecture, Multi Module, MVVM  | [프로젝트 구조](https://tral-lalala.tistory.com/126)⎮[build-logic](https://algosketch.tistory.com/179)⎮[네트워크 예외처리](https://github.com/boostcampwm2023/and04-catchy-tape/wiki/%EB%84%A4%ED%8A%B8%EC%9B%8C%ED%81%AC-%EC%98%88%EC%99%B8-%EC%B2%98%EB%A6%AC)
| DI | Hilt | 
| Network | Retrofit, OkHttp, Kotlin Serialization | [역/직렬화 라이브러리 비교](https://github.com/boostcampwm2023/and04-catchy-tape/wiki/%EC%97%AD-%EC%A7%81%EB%A0%AC%ED%99%94-%EB%9D%BC%EC%9D%B4%EB%B8%8C%EB%9F%AC%EB%A6%AC-%EB%B9%84%EA%B5%90)
| Asynchronous | Coroutines, Flow
| Jetpack | Media3, DataBinding, Navigation, DataStore | [Media Session](https://round-caution-9fd.notion.site/MediaSessionService-6397c6a060404250b7b565530e1004fd?pvs=4)
| Image | Glide
| CI/CD | Github Actions |[PR 단위 테스트 자동화](https://algosketch.tistory.com/178)⎮[Github Release 자동화](https://tral-lalala.tistory.com/127)⎮[Firebase App 배포 자동화](https://tral-lalala.tistory.com/128)
| Test | Kotest | [Kotest 도입기](https://github.com/boostcampwm2023/and04-catchy-tape/wiki/Kotest-%EB%8F%84%EC%9E%85%EA%B8%B0)
| Logging | Timber | [Timber 적용 이유](https://github.com/boostcampwm2023/and04-catchy-tape/wiki/Timber%EC%9D%84-%EC%A0%81%EC%9A%A9%ED%95%9C-%EC%9D%B4%EC%9C%A0)
- 🔧 Architecture
<img width="450" alt="image" src="https://github.com/boostcampwm2023/and04-catchy-tape/assets/62279741/6ba75222-1cd6-417a-b8ec-2aef6bbf1deb">

<details>
<summary>✏️ 그 외 기록</summary>

- [프로젝트 생성](https://github.com/boostcampwm2023/and04-catchy-tape/wiki/Android#%ED%94%84%EB%A1%9C%EC%A0%9D%ED%8A%B8-%EC%83%9D%EC%84%B1%EC%8B%9C-%EA%B3%A0%EB%A0%A4%ED%95%9C-%EB%82%B4%EC%9A%A9)

</details>


### 📡 Server
| Category  | TechStack | 기록 |
| ------------- | ------------- | -------------|
| Framework, Language | NestJS, TypeScript  | [Nest 사용 이유](https://round-caution-9fd.notion.site/Nest-8da8116bb8014a95b268ea50c9080b8d) |
| DB & ORM | MySQL & TypeORM  | [TypeORM 사용 이유](https://round-caution-9fd.notion.site/prisma-vs-TypeORM-0c8c89b3d5374405aca9e9c1db0a73b6)⎮ [관련 개념 학습](https://round-caution-9fd.notion.site/Server-624068a499114a14ba1388e198bb0dde?p=09210f9a985246639720c50e269f70a5&pm=s)|
| Test & Load Test | Jest & k6  | [부하 테스트 일대기](https://round-caution-9fd.notion.site/174440e709e24d7c909e8c1684c1cc75)⎮ [부하 테스트 결과 기록지](https://round-caution-9fd.notion.site/4ead90a8131844c8b561a34908692e3c)
| API Docs | SwaggerHub  | [Swagger Hub 링크](https://app.swaggerhub.com/apis/12201944/CatchyTapeImsi/1.0.0)
| CI/CD | Github Actions  | [Github Actions 활용한 자동 배포](https://round-caution-9fd.notion.site/Github-Action-29d0d57f5a434954b4a7b4aa8c3b57e0) |
| NCP | Server, Container Registry, VPC, Object Storage, CLOVA GreenEye, Cloud Functions|[vpc 환경 구성](https://round-caution-9fd.notion.site/VPC-a8eefbec2f0244629ee2a092c454ebd7) |
| Logging | Winston  | [로깅 이미지](https://github.com/boostcampwm2023/and04-catchy-tape/wiki/%EC%84%9C%EB%B2%84-%EB%A1%9C%EA%B9%85-%EC%9D%B4%EB%A0%87%EA%B2%8C-%ED%96%88%EC%96%B4%EC%9A%94-!)|
| 기술적 도전 |음악 인코딩, 인덱싱, docker 활용 배포, 부하 테스트| [인코딩](https://round-caution-9fd.notion.site/Cloud-Functions-d7f1528dd32146f6b8f0b255ef33ebd7)⎮[인덱싱](https://round-caution-9fd.notion.site/ERD-DB-74377ed10a2347d1ac15f181134f52a1)⎮[배포](https://round-caution-9fd.notion.site/docker-aa7522e8c5cf4b9c9135d6f6b2114fd4) |
| 한 눈에 보는 서버 기술 스택 |  | [서버 기술 선정 이유](https://round-caution-9fd.notion.site/2a7b52b27e7d45cc980c1eea33a2ce09) |

- 🔧 Architecture
<img src="https://github.com/boostcampwm2023/and04-catchy-tape/assets/83707411/33a0c12d-22da-4ae3-836b-3aeb46015183" width=600 height=400 />

<details>
<summary> ✏️ 그 외 기록</summary>
    <a href="https://round-caution-9fd.notion.site/85dad9cc5a304161bfde523f62345e05">인코딩 성능 개선기</a>
    <br>
    <a href="https://round-caution-9fd.notion.site/SSH-Private-DB-9965141c545849a8bba9f2ad066bc959">ssh 터널링</a>
</details>

## Team. 🍗 오도독 

|[J043_김형운](https://github.com/khw3754)|[J128_임서경](https://github.com/Cutiepazzipozzi)|[K013_박유라](https://github.com/youlalala)|[K018_송준영](https://github.com/HamBP)|[K031_이태경](https://github.com/2taezeat)|
|:---:|:---:|:---:|:---:|:---:|
|<img src="https://github.com/khw3754.png">|<img src="https://github.com/Cutiepazzipozzi.png">|<img src="https://github.com/youlalala.png">|<img src="https://github.com/HamBP.png">|<img src="https://github.com/2taezeat.png">|
|Backend|Backend|Android|Android|Android|
|강아지 귀여웡|엄마 뱃속으로 다시 들어가고 싶어요|hiphop은 계란이다 🥚|0과 1로 사람을 만들 수 있을까요?|Music is my life~|

<div align="center">
    <a href="https://github.com/boostcampwm2023/and04-catchy-tape/wiki/%08Ground-Rule" target="_blank">그라운드 룰</a>
    <a href="https://github.com/boostcampwm2023/and04-catchy-tape/wiki/Git-Convention" target="_blank">Git Convention</a>
</div>



