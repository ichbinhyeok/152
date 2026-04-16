@import gg.jte.Content
@param String currentPath
@param String pageTitle
@param String metaDescription
@param String canonicalUrl
@param boolean noindex
@param String teamSourceCheckedAt
@param int teamSourceCount
@param boolean showTeamTrust
@param Content content

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>${pageTitle}</title>
    <meta name="description" content="${metaDescription}">
    <link rel="canonical" href="${canonicalUrl}">
    @if(noindex)
        <meta name="robots" content="noindex,follow">
    @endif
    <link rel="icon" href="/favicon.svg" type="image/svg+xml">
    <link rel="shortcut icon" href="/favicon.svg" type="image/svg+xml">
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;700;800;900&family=Roboto+Mono:wght@400;500;700&display=swap" rel="stylesheet">
    <link href="https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined:wght,FILL@400,0&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="/main.css">
</head>
<body>
    <a class="skip-link" href="#mainContent">Skip to content</a>
    <header class="command-header">
        <div class="command-header__brand">
            <a class="brandmark" href="/">LL152.ENGINE</a>
            <p class="brandmark__meta">NYC compliance decision ledger</p>
        </div>
        <nav class="command-nav" aria-label="Primary">
            <a href="/" class="command-nav__link ${currentPath.equals("/") ? "is-active" : ""}">Home</a>
            <a href="/ll152-checker/" class="command-nav__link ${currentPath.equals("/ll152-checker/") ? "is-active" : ""}">Checker</a>
            <a href="/filing-next-step/" class="command-nav__link ${!currentPath.equals("/") && !currentPath.equals("/ll152-checker/") && !currentPath.equals("/admin") ? "is-active" : ""}">Routes</a>
        </nav>
        <div class="command-header__tools" aria-hidden="true">
            <span class="command-badge">Guide live</span>
            <span class="material-symbols-outlined">account_tree</span>
            <span class="material-symbols-outlined">sync</span>
            <span class="material-symbols-outlined">settings</span>
        </div>
    </header>

    <aside class="command-rail">
        <div class="command-rail__head">
            <p class="command-rail__label">Sovereign</p>
            <p class="command-rail__version">v.2.6.0 stable</p>
        </div>
        <nav class="command-rail__nav" aria-label="Workspace">
            <a href="/" class="command-rail__link ${currentPath.equals("/") ? "is-active" : ""}">
                <span class="material-symbols-outlined">space_dashboard</span>
                <span>Home</span>
            </a>
            <a href="/ll152-checker/" class="command-rail__link ${currentPath.equals("/ll152-checker/") ? "is-active" : ""}">
                <span class="material-symbols-outlined">gavel</span>
                <span>Run checker</span>
            </a>
            <a href="/filing-next-step/" class="command-rail__link ${!currentPath.equals("/") && !currentPath.equals("/ll152-checker/") && !currentPath.equals("/admin") ? "is-active" : ""}">
                <span class="material-symbols-outlined">account_tree</span>
                <span>Route guides</span>
            </a>
        </nav>
        <div class="command-rail__footer">
            <a class="button button--rail" href="/ll152-checker/">Run checker</a>
            <div class="command-rail__status">
                <span>Status: ready</span>
                <span>Registry: synced</span>
            </div>
        </div>
    </aside>

    <main class="app-main" id="mainContent" tabindex="-1">
        ${content}
    </main>

    @if(showTeamTrust)
        <section class="team-trust">
            <div class="team-trust__intro">
                <p class="mono-label">Guidance team</p>
                <h2>LL152 Engine team</h2>
                <p>The team maintains this guidance surface as a source-checked decision aid for NYC Local Law 152 routing. It is built to help users narrow the next step before they act in DOB NOW.</p>
            </div>
            <div class="team-trust__grid">
                <div class="team-trust__item">
                    <span class="mono-label">Current source check</span>
                    <strong>${teamSourceCheckedAt.isBlank() ? "Current cycle" : teamSourceCheckedAt}</strong>
                </div>
                <div class="team-trust__item">
                    <span class="mono-label">Official source set</span>
                    <strong>${teamSourceCount} linked references</strong>
                </div>
                <div class="team-trust__item">
                    <span class="mono-label">Boundary</span>
                    <strong>Routing aid, not legal advice or DOB filing sign-off</strong>
                </div>
            </div>
        </section>
    @endif

    <footer class="app-footer">
        <p>Likely verdict first. Verify every filing against current DOB guidance.</p>
        <div class="footer-links">
            <a href="/methodology/">Methodology</a>
            <a href="/privacy-data-use/">Privacy</a>
            <a href="/response-policy/">Response policy</a>
            <a href="/not-government-affiliated/">Not government-affiliated</a>
            <a href="/contact/">Contact</a>
        </div>
    </footer>

    <aside class="lead-drawer" id="leadDrawer" aria-hidden="true" aria-modal="true" aria-labelledby="leadDrawerTitle" aria-describedby="leadDrawerSummary" role="dialog">
        <div class="lead-drawer__panel" tabindex="-1">
            <div class="lead-drawer__header">
                <div>
                    <p class="mono-label">Review intake</p>
                    <h2 id="leadDrawerTitle">Request priority review</h2>
                </div>
                <button class="ghost-button" type="button" id="leadCloseButton" aria-label="Close lead form">Close</button>
            </div>
            <p class="lead-drawer__summary" id="leadDrawerSummary">Use this when the blocker is already specific and you want the team to confirm the likely route before the case moves to filing, inspection, or certification help.</p>
            <ul class="checklist-list lead-drawer__checklist">
                <li>The team checks the likely branch and the missing filing fact first.</li>
                <li>Cases that still need manual help can be routed to the right partner after review.</li>
                <li>Do not send DOB passwords, payment data, or emergency gas reports through this form.</li>
            </ul>
            <form class="lead-form" id="leadForm">
                <input type="hidden" name="routeId" id="leadRouteId">
                <input type="hidden" name="routePath" id="leadRoutePath" value="/">
                <input type="hidden" name="intent" id="leadIntent" value="filing_help">
                <input type="hidden" name="scenarioKey" id="leadScenarioKey">

                <label>
                    Name
                    <input type="text" name="name" required>
                </label>
                <label>
                    Email
                    <input type="email" name="email" required>
                </label>
                <label>
                    Phone
                    <input type="text" name="phone">
                </label>
                <label>
                    Building address or ID
                    <input type="text" name="buildingAddress" id="leadBuildingAddress" required>
                </label>
                <label>
                    What still needs review?
                    <textarea name="message" rows="4"></textarea>
                </label>

                <div class="lead-form__actions">
                    <button class="button" type="submit" id="leadSubmitButton">Request priority review</button>
                    <p class="form-status" id="leadFormStatus" aria-live="polite"></p>
                </div>
            </form>
        </div>
    </aside>

    <script src="/app.js"></script>
</body>
</html>
