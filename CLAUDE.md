## Deploy Configuration (configured by /setup-deploy)
- Platform: GitHub Actions to Docker Hub to OCI host behind nginx
- Production URL: https://ll152guide.com
- Deploy workflow: .github/workflows/deploy.yml
- Deploy status command: docker compose ps
- Merge method: merge to main
- Project type: web app
- Post-deploy health check: https://ll152guide.com/robots.txt

### Custom deploy hooks
- Pre-merge: ./gradlew --no-daemon test
- Deploy trigger: automatic on push to main or manual workflow_dispatch
- Deploy status: GitHub Actions workflow plus OCI docker compose rollout
- Health check: http://127.0.0.1:8099/robots.txt plus an HTTPS host-routed check for ll152guide.com
