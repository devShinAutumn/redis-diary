# Redis Diary 📒

> **“생각을 기록하고, 감정을 캐싱하다.”**  
> 감정 일기를 작성하면 OpenAI가 감정을 분석하고, Redis가 결과를 캐싱해 빠르게 보여주는 스마트 다이어리 서비스입니다.  
> 자주 호출되는 데이터를 Redis에 저장해 **응답 속도를 최대 90% 향상**시키고 서버 부하를 줄였습니다.

---

## 🚀 주요 기능
- ✍️ **일기 작성** — 하루의 생각을 기록  
- 🤖 **AI 감정 분석** — OpenAI Chat Completions로 감정 자동 분류  
- ⚡ **Redis 캐싱** — 최근 일기·감정 결과를 캐싱해 빠른 응답 제공  
- 📊 **감정 통계 시각화** — Redis 기반 주간 감정 분포  
- 🧱 **풀스택 구성** — Spring Boot + PostgreSQL + Redis + React + Docker

---

## 🧩 기술 스택
**Backend**: Spring Boot, JPA, Redis, OpenAI API, Maven  
**Frontend**: React (Vite)  
**Infra**: Docker Compose (Postgres + Redis + App)  

---

## ⚙️ 캐싱 전략
| 캐시 키 | TTL | 설명 |
|----------|-----|------|
| `diary:{id}` | 15m | 특정 일기 상세 조회 |
| `diary:recent:{userId}` | 5m | 최근 일기 목록 |
| `emotion:diary:{id}` | 1h | 감정 분석 결과 |

---

## 💬 서비스 흐름
1. 사용자가 일기를 작성합니다.  
2. 서버가 OpenAI Chat Completions API로 감정을 분석합니다.  
3. 분석 결과를 Redis에 캐싱합니다.  
4. 다음 요청부터는 DB 대신 Redis에서 즉시 응답합니다.  

→ 캐시 전후 응답 속도 차이를 측정해보면,  
**초회 300ms → 재호출 30ms 이하**로 단축됩니다.

---

## 🛠️ 실행 방법
```bash
export OPENAI_API_KEY="sk-xxxx"
docker-compose up --build
