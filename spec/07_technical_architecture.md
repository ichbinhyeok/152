# 07 Technical Architecture

## Build goal
Implement a server-rendered checker and compliance site with a file-backed data pipeline and simple lead capture.

## Suggested package root
- `src/main/java/owner/nycll152`
- `src/test/java/owner/nycll152`

## Suggested package map
- `owner.nycll152.data`
- `owner.nycll152.ingest`
- `owner.nycll152.pages`
- `owner.nycll152.checker`
- `owner.nycll152.leads`
- `owner.nycll152.web`
- `owner.nycll152.ops`

## Core services
- repository layer
- checker engine
- route builder
- page service
- lead service
- event logger

## Recommended storage
- `storage/leads/leads.csv`
- `storage/leads/lead_events.csv`
- `storage/ops/*.json`

## Suggested runtime endpoints
- `GET /`
- `GET /ll152-checker/`
- `POST /api/checker/run`
- `GET /{route}/`
- `POST /api/leads/capture`
- `POST /api/leads/event`
- `GET /admin`
