unit:
	cd backend && ./gradlew clean build -x itTest

it:
	cd backend && ./gradlew itTest

all:
	make unit && make it && make fe

fe:
	cd frontend && npm ci && npm run build && npm test


