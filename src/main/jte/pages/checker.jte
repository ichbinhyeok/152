@import java.util.List
@import owner.nycll152.checker.BuildingProfile
@import owner.nycll152.pages.RoutePage
@param RoutePage page
@param String currentPath
@param String siteUrl
@param String canonicalUrl
@param String teamSourceCheckedAt
@param int teamSourceCount
@param boolean showTeamTrust
@param String breadcrumbStructuredData
@param String webPageStructuredData
@param List<RoutePage> relatedRoutes
@param BuildingProfile[] profiles

@template.layout.page(
    currentPath = currentPath,
    pageTitle = page.title(),
    metaDescription = page.summary(),
    canonicalUrl = canonicalUrl,
    noindex = !page.indexable(),
    teamSourceCheckedAt = teamSourceCheckedAt,
    teamSourceCount = teamSourceCount,
    showTeamTrust = showTeamTrust,
    content = @`
        <script type="application/ld+json">
            $unsafe{breadcrumbStructuredData}
        </script>
        <script type="application/ld+json">
            $unsafe{webPageStructuredData}
        </script>
        <section class="checker-board">
            <article class="ledger-panel">
                <div class="ledger-panel__header">
                    <div>
                        <p class="mono-label">Building details</p>
                        <h1>${page.title()}</h1>
                    </div>
                    <p class="ledger-panel__intro">${page.hero()}</p>
                </div>

                <form class="checker-form" id="checkerForm">
                    <label class="ledger-field">
                        <span>Building address</span>
                        <input type="text" name="address" id="checkerAddress" placeholder="Building address">
                    </label>
                    <div class="checker-form__pair">
                        <label class="ledger-field">
                            <span>BIN</span>
                            <input type="text" name="bin" placeholder="Optional BIN">
                        </label>
                        <label class="ledger-field">
                            <span>BBL</span>
                            <input type="text" name="bbl" placeholder="Optional BBL">
                        </label>
                    </div>
                    <p class="form-note">Address, BIN, and BBL help identify the building, but the checker still needs at least one routing detail: district, property class, building type, or gas status.</p>
                    <div class="checker-form__pair">
                        <label class="ledger-field">
                            <span>Property class</span>
                            <input type="text" name="dofClass" placeholder="Optional A6, C1, M3">
                        </label>
                        <label class="ledger-field">
                            <span>Building type</span>
                            <select name="buildingType">
                                @for(var profile : profiles)
                                    <option value="${profile.value()}">${profile.label()}</option>
                                @endfor
                            </select>
                        </label>
                    </div>
                    <div class="checker-form__pair checker-form__pair--triple">
                        <label class="ledger-field">
                            <span>Community district</span>
                            <input type="number" name="communityDistrict" min="1" max="18" placeholder="Optional district">
                        </label>
                        <label class="ledger-field">
                            <span>Gas piping</span>
                            <select name="gasPiping">
                                <option value="">I need a general check</option>
                                <option value="true">Gas piping is present</option>
                                <option value="false">No gas piping</option>
                            </select>
                        </label>
                        <label class="ledger-field">
                            <span>Active gas service</span>
                            <select name="activeGasService">
                                <option value="">Set gas piping to present first</option>
                                <option value="true">Active gas service</option>
                                <option value="false">No active gas service</option>
                            </select>
                        </label>
                    </div>
                    <div class="checker-form__actions">
                        <button class="button" type="submit">Run checker</button>
                        <p class="form-status" id="checkerStatus" aria-live="polite">Ready when you are.</p>
                    </div>
                </form>

                <div class="integrity-panel">
                    <p class="mono-label">What changes the answer</p>
                    <p>Use only the fields that change coverage, due-cycle timing, or the no-gas versus no-active-service branch.</p>
                    <p class="result-note">Source check: ${teamSourceCheckedAt}. Current rule set: ${teamSourceCount} official references.</p>
                </div>

                <div class="integrity-ledger">
                    <p class="mono-label">Why certainty changes</p>
                    <p>Coverage, district, and gas status drive the primary branch. Missing any of the three should lower certainty before it lowers trust.</p>
                    <ul class="checklist-list">
                        <li>Confirm building identity before escalation.</li>
                        <li>Separate gas piping from active gas service.</li>
                        <li>Open the route only after the verdict stabilizes.</li>
                    </ul>
                </div>
            </article>

            <aside class="checker-result" id="checkerResult">
                <div class="verdict-header">
                    <div>
                        <p class="mono-label">Likely next step</p>
                        <h2>Route result</h2>
                    </div>
                    <div class="confidence-readout">
                        <span id="resultConfidence">Awaiting input</span>
                        <small>Confidence rating</small>
                    </div>
                </div>

                <div class="decision-ribbon">
                    <div>
                        <p class="mono-label mono-label--light">What to do next</p>
                        <h3 id="resultNextStep">Run the checker to render the next action.</h3>
                    </div>
                    <span class="decision-ribbon__ref">LL152 route</span>
                </div>

                <div class="verdict-grid">
                    <article class="verdict-block">
                        <p class="mono-label">Coverage</p>
                        <h3 id="resultCoverage">Verdict pending</h3>
                        <p id="resultDueCycle">Cycle timing appears here after the property ledger is validated.</p>
                    </article>
                    <article class="verdict-block">
                        <p class="mono-label">Why this answer</p>
                        <p id="resultSummary">Use the ledger on the left to calculate the likely next filing step.</p>
                        <ul class="rationale-list" id="resultRationale">
                            <li>Coverage logic renders after input validation.</li>
                            <li>Gas-status branching populates the directive ribbon.</li>
                        </ul>
                        <p class="result-note" id="resultSourceNote">Official sources attach to the rendered verdict.</p>
                    </article>
                </div>

                <div class="verdict-actions">
                    <a class="button" id="resultRouteLink" href="/filing-next-step/">Review the recommended route</a>
                    <button class="button button--secondary" id="resultLeadButton" type="button" data-lead-open data-route-id="${page.id()}" data-route-path="${page.path()}" data-intent="filing_help">Send case details</button>
                </div>

                <div class="verdict-grid verdict-grid--secondary">
                    <article class="verdict-block">
                        <p class="mono-label">Do next</p>
                        <ul class="checklist-list" id="resultChecklist">
                            <li>Run the verdict engine.</li>
                            <li>Review the rendered route.</li>
                            <li>Prepare the next filing inputs.</li>
                        </ul>
                    </article>
                    <article class="verdict-block">
                        <p class="mono-label">What still controls this answer</p>
                        <p class="result-note" id="resultConfidenceReason">Add the core branch inputs to see which fact still changes the answer.</p>
                    </article>
                    <article class="verdict-block">
                        <p class="mono-label">Pause before filing when</p>
                        <p class="result-note" id="resultReviewBoundary">Pause when a single unresolved fact can still flip the route.</p>
                    </article>
                    <article class="verdict-block">
                        <p class="mono-label">Official source anchors</p>
                        <ul class="source-list" id="resultSources">
                            <li>
                                <span>Source anchors appear after the verdict is rendered.</span>
                            </li>
                        </ul>
                    </article>
                </div>

                <figure class="reference-figure reference-figure--checker">
                    <img src="https://images.unsplash.com/photo-1771153511650-095180992e08?auto=format&fit=crop&fm=jpg&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&ixlib=rb-4.1.0&q=60&w=1600" alt="Architectural blueprint drawing applied to a city building surface.">
                </figure>
            </aside>
        </section>

        <section class="register-panel register-panel--wide">
            <div class="section-bar">
                <div>
                    <p class="mono-label">Related routes</p>
                    <h2>Keep moving toward the actual filing step.</h2>
                </div>
            </div>
            <div class="ledger-card-grid ledger-card-grid--compact">
                @for(var route : relatedRoutes)
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
    `
)
