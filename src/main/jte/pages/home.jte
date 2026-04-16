@import java.util.List
@import owner.nycll152.pages.HomeBlockerCard
@import owner.nycll152.pages.RoutePage
@param String currentPath
@param String siteUrl
@param String canonicalUrl
@param String teamSourceCheckedAt
@param int teamSourceCount
@param boolean showTeamTrust
@param String organizationStructuredData
@param String websiteStructuredData
@param List<HomeBlockerCard> blockerCards
@param List<RoutePage> coreRoutes
@param List<RoutePage> trustRoutes
@param List<RoutePage> heldRoutes

@template.layout.page(
    currentPath = currentPath,
    pageTitle = "NYC Local Law 152 checker: does this building need inspection?",
    metaDescription = "Use the NYC Local Law 152 checker to see whether a building likely needs inspection, which cycle applies, and whether the next step is no gas piping, no active gas service, GPS1, GPS2, or route review.",
    canonicalUrl = canonicalUrl,
    noindex = false,
    teamSourceCheckedAt = teamSourceCheckedAt,
    teamSourceCount = teamSourceCount,
    showTeamTrust = showTeamTrust,
    content = @`
        <script type="application/ld+json">
            $unsafe{organizationStructuredData}
        </script>
        <script type="application/ld+json">
            $unsafe{websiteStructuredData}
        </script>
        <section class="dashboard-hero">
            <div class="dashboard-hero__head">
                <div>
                    <p class="eyebrow">Decision engine / ll152</p>
                    <h1>Know the<br>next real<br>LL152 decision<br>first.</h1>
                    <p class="dashboard-hero__summary">This is not a broad explainer. It narrows the building into the three facts that actually change the outcome: coverage, the current due window, and the gas-status filing branch.</p>
                    <div class="signal-row">
                        <span class="signal-row__item">Coverage branch first</span>
                        <span class="signal-row__item">Due-window map active</span>
                        <span class="signal-row__item">Gas-status split enforced</span>
                        <span class="signal-row__item">Team source check ${teamSourceCheckedAt}</span>
                    </div>
                </div>
                <div class="document-meta">
                    <span>Current locus</span>
                    <strong>NY_DOB_NODE_01</strong>
                    <span>Registry state: uninterrupted</span>
                </div>
            </div>
        </section>

        <section class="command-grid command-grid--home">
            <article class="command-panel command-panel--primary">
                <div class="panel-rule">
                    <span>Engine state: ready</span>
                    <div></div>
                </div>
                <h2>Settle the building's<br>next real<br>LL152 decision.</h2>
                <p>Start here when the question is still broad. The checker tells you whether this is a coverage question, a due-now question, or a filing-path question.</p>
                <div class="command-panel__actions">
                    <a class="button button--inverse" href="/ll152-checker/">Run checker</a>
                    <button class="ghost-link ghost-link--light" type="button" data-lead-open data-route-id="home" data-route-path="/" data-intent="filing_help">Request priority review</button>
                </div>
                <div class="panel-meta">
                    <span>Works with partial building facts</span>
                    <span>Built to prevent wrong-path filings</span>
                </div>
            </article>

            <aside class="command-panel command-panel--secondary">
                <div class="panel-heading">
                    <p class="mono-label">Decision package</p>
                    <h3>What the engine settles first</h3>
                </div>
                <ul class="status-list">
                    <li><span>Coverage branch</span><strong>See whether the building likely belongs on the covered side or the exempt side</strong></li>
                    <li><span>Due window</span><strong>See whether 2026 is live, future, or already behind the building</strong></li>
                    <li><span>Gas-status route</span><strong>Separate no gas piping, no active service, and active service before filing</strong></li>
                    <li><span>Source anchors</span><strong>Carry DOB-linked references into the decision before you escalate</strong></li>
                </ul>
                <figure class="reference-figure reference-figure--home">
                    <img src="https://images.unsplash.com/photo-1765646663860-5ac4895f2d21?auto=format&fit=crop&fm=jpg&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&ixlib=rb-4.1.0&q=60&w=1600" alt="White New York apartment facade with repetitive windows and a fire escape.">
                </figure>
            </aside>
        </section>

        <section class="ledger-section ledger-section--home" id="pick-the-blocker">
            <div class="section-bar">
                <div>
                    <p class="mono-label">Most searched paths</p>
                    <h2>Start from the question owners actually search.</h2>
                </div>
                <div class="section-badges">
                    <span>Total: ${blockerCards.size()}</span>
                    <span>Engine linked: ${coreRoutes.size()}</span>
                </div>
            </div>
            <div class="ledger-card-grid">
                @for(var route : coreRoutes)
                    <article class="ledger-card">
                        <div class="ledger-card__meta">
                            <span class="mono-label">${route.id()}</span>
                            <span class="eyebrow">${route.eyebrow()}</span>
                        </div>
                        <h3>${route.title()}</h3>
                        <p>${route.summary()}</p>
                        <div class="ledger-card__footer">
                            <span class="mono-label">Path active</span>
                            <div class="ledger-card__actions">
                                <a class="text-link" href="${route.path()}">Review route</a>
                                <button class="ghost-button" type="button" data-lead-open data-route-id="${route.id()}" data-route-path="${route.path()}" data-intent="${route.ctaIntent()}">${route.ctaLabel()}</button>
                            </div>
                        </div>
                    </article>
                @endfor
            </div>
        </section>

        <section class="register-grid register-grid--home">
            <article class="register-panel register-panel--emphasis">
                <div class="panel-heading">
                    <p class="mono-label">Blocker first</p>
                    <h3>Start with the first blocking question.</h3>
                </div>
                <ul class="register-list">
                    @for(var blocker : blockerCards)
                        <li>
                            <a href="${blocker.path()}">${blocker.title()}</a>
                            <span>${blocker.summary()}</span>
                        </li>
                    @endfor
                </ul>
            </article>

            <article class="register-panel register-panel--tone">
                <div class="panel-heading">
                    <p class="mono-label">Proceed directly</p>
                    <h3>You can keep moving yourself when the branch is already explicit.</h3>
                </div>
                <p>Use the route without review when the remaining work is document gathering, timing, or form sequence rather than deciding what path applies.</p>
                <ul class="checklist-list">
                    <li>The building looks clearly covered or clearly exempt.</li>
                    <li>The district timing is explicit or obviously not the live blocker.</li>
                    <li>The gas-status branch is already settled.</li>
                    <li>The remaining question is what to prepare next, not which route is correct.</li>
                </ul>
            </article>

            <article class="register-panel register-panel--quiet">
                <div class="panel-heading">
                    <p class="mono-label">Priority review</p>
                    <h3>Use team review before the wrong filing becomes the problem.</h3>
                </div>
                <p>Use priority review when one unresolved fact can still flip the route, the due window, or the filing path. The team confirms the branch first, then points the case to the right inspection or certification partner if manual help is still needed.</p>
                <ul class="checklist-list">
                    <li>The next filing step is still unclear even though coverage looks likely.</li>
                    <li>No gas piping versus no active gas service is still unresolved.</li>
                    <li>DOB or DOF records still conflict with what you believe the building is.</li>
                    <li>Inspection timing, GPS1, GPS2, or corrections now control the next move.</li>
                    <li>The due window may have passed and penalty or waiver risk is now part of the decision.</li>
                    <li>You want route confirmation before reaching out to an inspection or certification partner.</li>
                </ul>
            </article>
        </section>

        @if(!trustRoutes.isEmpty())
            <section class="register-panel register-panel--wide">
                <div class="section-bar">
                    <div>
                        <p class="mono-label">Operating notes</p>
                        <h2>Read the trust, privacy, and follow-up rules before you share a building.</h2>
                    </div>
                </div>
                <div class="ledger-card-grid ledger-card-grid--compact">
                    @for(var route : trustRoutes)
                        <article class="ledger-card">
                            <div class="ledger-card__meta">
                                <span class="mono-label">${route.id()}</span>
                                <span class="eyebrow">${route.eyebrow()}</span>
                            </div>
                            <h3><a href="${route.path()}">${route.title()}</a></h3>
                            <p>${route.summary()}</p>
                        </article>
                    @endfor
                </div>
            </section>
        @endif
    `
)
