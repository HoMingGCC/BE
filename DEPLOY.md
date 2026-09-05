# AWS EC2 배포 가이드

BE를 EC2(Docker) + RDS(MySQL)로 띄우는 순서입니다. 코드 쪽 준비(Dockerfile, `application-prod.yaml`, CORS 허용 목록)는 이미 되어 있고, 아래는 AWS 콘솔에서 직접 할 작업입니다.

## 0. 미리 알아둘 것 — HTTPS가 필수인 이유

FE(`https://fe-6ab3.vercel.app`)는 HTTPS로 떠 있습니다. 브라우저는 HTTPS 페이지에서 HTTP API를 호출하는 걸 mixed content로 차단하기 때문에, **EC2를 그냥 HTTP(포트 8080)로만 열어두면 배포된 FE에서는 호출이 안 됩니다.** 그래서 3번(Nginx + HTTPS)까지 마쳐야 실제 연동이 됩니다.

다만 로컬에서 FE 개발 서버(`http://localhost:5173`)로 테스트하는 건 HTTP끼리라 mixed content 문제가 없으니, 2번까지만 끝나도 로컬 개발 중인 주연이 EC2의 `http://<EC2 IP>:8080`을 바로 붙여볼 수 있습니다.

**HTTPS를 하려면 도메인이 필요합니다** (Let's Encrypt는 도메인 소유 검증 방식이라 EC2 기본 퍼블릭 DNS로는 발급이 안 됩니다). 가진 도메인이 없다면 Route 53이나 다른 등록기관에서 저렴한 도메인 하나를 사거나, 팀에서 이미 쓰는 도메인의 서브도메인(`api.homing.example.com` 등)을 하나 내주세요.

## 1. RDS (MySQL) 생성

1. RDS 콘솔 → 데이터베이스 생성 → 엔진: MySQL 8.x
2. 템플릿: 프리티어(해커톤이면 충분) 또는 개발/테스트
3. DB 인스턴스 식별자: `homing-db`, 마스터 사용자명/비밀번호 설정 (비밀번호는 따로 기록해두세요 — `.env.production`에 넣을 값)
4. **퍼블릭 액세스: 아니오** 권장. 대신 EC2와 같은 VPC에 두고, RDS 보안 그룹의 인바운드 규칙에서 **EC2 보안 그룹**을 소스로 3306 포트를 허용하세요. (같은 VPC 안에서만 통신 — 외부에 DB를 직접 노출하지 않기 위함)
5. 초기 데이터베이스 이름: `gcc`
6. 생성 후 엔드포인트 주소를 기록 (`homing-db.xxxxxxxx.<region>.rds.amazonaws.com` 형태)

## 2. EC2 생성 + Docker 실행

1. EC2 콘솔 → 인스턴스 시작
   - AMI: Amazon Linux 2023
   - 인스턴스 유형: t3.micro (프리티어) 이상
   - 보안 그룹: 22(SSH, 본인 IP만), 80, 443 허용. **8080은 외부에 열 필요 없음** (Nginx가 앞단에서 받아 내부적으로 8080에 전달)
   - RDS와 같은 VPC로 생성
2. SSH 접속 후 Docker 설치
   ```bash
   sudo dnf update -y
   sudo dnf install -y docker git
   sudo systemctl enable --now docker
   sudo usermod -aG docker ec2-user
   # 재접속(그룹 반영)
   ```
3. 코드 가져와서 이미지 빌드
   ```bash
   git clone https://github.com/HoMingGCC/BE.git
   cd BE
   cp .env.production.example .env.production
   vi .env.production   # DB_URL(위 RDS 엔드포인트로), DB_USERNAME, DB_PASSWORD, NTS_SERVICE_KEY, KAKAO_REST_KEY 채우기
   docker build -t homing-be .
   ```
4. 컨테이너 실행
   ```bash
   docker run -d --name homing-be \
     --restart always \
     --env-file .env.production \
     -p 8080:8080 \
     homing-be
   ```
5. 로그/헬스체크
   ```bash
   docker logs -f homing-be           # "Started ImpossibleApplication" 확인
   curl http://localhost:8080/api/stores
   ```

이 시점에서 `http://<EC2 퍼블릭 IP>:8080/api/stores`가 브라우저/curl로 열리면 컨테이너는 정상입니다. (보안그룹에서 8080을 임시로 열어야 외부에서 바로 테스트 가능 — 확인 후엔 닫고 Nginx만 쓰는 걸 권장)

## 3. Nginx + Let's Encrypt로 HTTPS 붙이기

도메인의 A 레코드를 EC2 퍼블릭 IP(탄력적 IP 권장 — 재부팅해도 안 바뀜)로 먼저 연결해두세요.

```bash
sudo dnf install -y nginx
sudo systemctl enable --now nginx

# /etc/nginx/conf.d/homing-be.conf
sudo tee /etc/nginx/conf.d/homing-be.conf <<'EOF'
server {
    listen 80;
    server_name api.yourdomain.com;   # 실제 도메인으로 교체

    location / {
        proxy_pass http://127.0.0.1:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
EOF
sudo nginx -t && sudo systemctl reload nginx

# 인증서 발급 (certbot)
sudo dnf install -y python3-certbot-nginx
sudo certbot --nginx -d api.yourdomain.com
```

certbot이 nginx 설정에 443/TLS를 자동으로 추가해줍니다. 완료 후:

```bash
curl https://api.yourdomain.com/api/stores
```

## 4. FE에 전달할 정보

- API base URL: `https://api.yourdomain.com` (도메인 확정되면 알려주세요 — 코드엔 이미 `https://fe-6ab3.vercel.app` CORS 허용이 되어 있어서 BE 쪽은 추가로 손댈 게 없습니다)
- 인증 없음 (전 엔드포인트 공개) — 데모 단계라 별도 로그인/토큰 불필요

## 5. 배포 후 시드 데이터 확인

컨테이너가 처음 뜰 때 `DemoDataSeeder`가 RDS에 가게 12곳·결제 146건 등을 자동으로 넣습니다 (재기동 시 중복 삽입 안 함). `docker logs homing-be`에서 "시드 완료" 로그로 확인하세요.

## 업데이트 배포 (재배포) — 수동

```bash
cd BE && git pull
docker build -t homing-be .
docker stop homing-be && docker rm homing-be
docker run -d --name homing-be --restart always --env-file .env.production -p 8080:8080 homing-be
```

## 6. GitHub Actions로 자동 배포 (CI/CD)

`main` 브랜치에 push하면 GitHub Actions가 이미지를 빌드해 Docker Hub에 push하고, EC2에 SSH로 접속해 컨테이너를 새 이미지로 교체합니다 (워크플로우: `.github/workflows/deploy.yml`). EC2에서 직접 Gradle 빌드를 하지 않으므로 t3.micro에서도 느려지거나 메모리 부족으로 실패할 일이 없습니다.

### 6-1. EC2 쪽 준비 (최초 1회)

기존 수동 배포와 달리 이제 EC2에 소스코드를 클론해둘 필요는 없고, 컨테이너 환경변수 파일만 홈 디렉터리에 있으면 됩니다.

```bash
# EC2에 SSH 접속 후
cp .env.production.example ~/.env.production   # 또는 직접 생성
vi ~/.env.production   # DB_URL, DB_USERNAME, DB_PASSWORD, NTS_SERVICE_KEY, KAKAO_REST_KEY 채우기
```

GitHub Actions가 SSH로 접속할 수 있도록 배포 전용 키 페어를 준비하세요.

```bash
# 로컬(또는 EC2)에서 배포 전용 키 생성
ssh-keygen -t ed25519 -f homing-deploy-key -N ""
# 공개키를 EC2의 authorized_keys에 추가
cat homing-deploy-key.pub >> ~/.ssh/authorized_keys   # EC2에서 실행
```

### 6-2. Docker Hub 준비

1. [hub.docker.com](https://hub.docker.com) 계정 준비
2. Account Settings → Security → New Access Token 발급 (Read & Write 권한)

### 6-3. GitHub 저장소 Secrets 등록

리포지토리 → Settings → Secrets and variables → Actions → New repository secret 에서 아래 값을 등록합니다.

| Secret 이름 | 값 |
|---|---|
| `DOCKERHUB_USERNAME` | Docker Hub 계정명 |
| `DOCKERHUB_TOKEN` | 위에서 발급한 Access Token |
| `EC2_HOST` | EC2 퍼블릭 IP 또는 탄력적 IP |
| `EC2_USERNAME` | `ec2-user` (Amazon Linux 기준) |
| `EC2_SSH_KEY` | 위에서 만든 배포 전용 키의 **개인키**(`homing-deploy-key`) 전체 내용 |

등록 후 `main`에 push하면 Actions 탭에서 빌드·배포 진행 상황을 확인할 수 있습니다.
