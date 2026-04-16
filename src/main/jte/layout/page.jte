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
    <link href="https://fonts.googleapis.com/css2?family=Fraunces:opsz,wght@9..144,600;9..144,700&family=Inter:wght@400;500;700;800;900&family=Roboto+Mono:wght@400;500;700&display=swap" rel="stylesheet">
    <link href="https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined:wght,FILL@400,0&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="/main.css">
</head>
<body>
    <a class="skip-link" href="#mainContent">Skip to content</a>
    <header class="command-header">
        <div class="command-header__brand">
            <a class="brandmark" href="/">LL152 Guidance Desk</a>
            <p class="brandmark__meta">Source-checked NYC filing guidance</p>
        </div>
        <nav class="command-nav" aria-label="Primary">
            <a href="/" class="command-nav__link ${currentPath.equals("/") ? "is-active" : ""}">Home</a>
            <a href="/ll152-checker/" class="command-nav__link ${currentPath.equals("/ll152-checker/") ? "is-active" : ""}">Checker</a>
            <a href="/filing-next-step/" class="command-nav__link ${!currentPath.equals("/") && !currentPath.equals("/ll152-checker/") && !currentPath.equals("/admin") && !currentPath.equals("/about/") ? "is-active" : ""}">Filing steps</a>
            <a href="/about/" class="command-nav__link ${currentPath.equals("/about/") ? "is-active" : ""}">Trust</a>
        </nav>
        <button class="button button--header" type="button" data-lead-open data-route-id="site-header" data-route-path="${currentPath}" data-intent="filing_help">Send case details</button>
    </header>

    <main class="app-main" id="mainContent" tabindex="-1">
        ${content}
    </main>

    @if(showTeamTrust)
        <section class="team-trust">
            <div class="team-trust__intro">
                <p class="mono-label">Operator</p>
                <h2>LL152 Guidance Desk</h2>
                <p>A virtual team maintaining a narrow, source-checked routing surface for NYC Local Law 152. Use it to narrow the next filing step before you act in DOB NOW.</p>
            </div>
            <div class="team-trust__grid">
                <div class="team-trust__item">
                    <span class="mono-label">Current source check</span>
                    <strong>${teamSourceCheckedAt.isBlank() ? "Current cycle" : teamSourceCheckedAt}</strong>
                </div>
                <div class="team-trust__item">
                    <span class="mono-label">Contact</span>
                    <strong>shinhyeok22@gmail.com</strong>
                </div>
                <div class="team-trust__item">
                    <span class="mono-label">Boundary</span>
                    <strong>Routing aid, not legal advice, not DOB, not filing sign-off</strong>
                </div>
            </div>
        </section>
    @endif

    <footer class="app-footer">
        <p>Likely verdict first. Verify every filing against current DOB guidance before you submit anything.</p>
        <div class="footer-links">
            <a href="/about/">About</a>
            <a href="/methodology/">Methodology</a>
            <a href="/privacy-data-use/">Privacy</a>
            <a href="/response-policy/">Response policy</a>
            <a href="/terms-disclaimer/">Terms and disclosure</a>
            <a href="/not-government-affiliated/">Not government-affiliated</a>
            <a href="/contact/">Contact</a>
            <a href="mailto:shinhyeok22@gmail.com">shinhyeok22@gmail.com</a>
        </div>
    </footer>

    <aside class="lead-drawer" id="leadDrawer" aria-hidden="true" aria-modal="true" aria-labelledby="leadDrawerTitle" aria-describedby="leadDrawerSummary" role="dialog">
        <div class="lead-drawer__panel" tabindex="-1">
            <div class="lead-drawer__header">
                <div>
                    <p class="mono-label">Case intake</p>
                    <h2 id="leadDrawerTitle">Send case details</h2>
                </div>
                <button class="ghost-button" type="button" id="leadCloseButton" aria-label="Close lead form">Close</button>
            </div>
            <p class="lead-drawer__summary" id="leadDrawerSummary">Use this when one missing fact still changes the filing path, due window, or gas-service branch. Sending the case now keeps the building facts, route, and blocker together before the wrong filing step creates delay.</p>
            <ul class="checklist-list lead-drawer__checklist">
                <li>Share only the building facts that still change the next step.</li>
                <li>The intake stores the route, blocker, and contact details together so the next reply can start from the same case context.</li>
                <li>For gas odor or unsafe conditions, use 911, your gas utility emergency line, or DOB emergency channels instead.</li>
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
                    What still blocks the next step?
                    <textarea name="message" rows="4"></textarea>
                </label>
                <p class="lead-form__consent">By sending this intake, you agree to the <a href="/privacy-data-use/">privacy notice</a>, <a href="/response-policy/">response policy</a>, and <a href="/terms-disclaimer/">terms and disclosure</a>. For corrections or deletion requests, email <a href="mailto:shinhyeok22@gmail.com">shinhyeok22@gmail.com</a>.</p>

                <div class="lead-form__actions">
                    <button class="button" type="submit" id="leadSubmitButton">Send case details</button>
                    <p class="form-status" id="leadFormStatus" aria-live="polite"></p>
                </div>
            </form>
        </div>
    </aside>

    <script src="/app.js"></script>
</body>
</html>
