@import owner.nycll152.ops.AdminSummary
@import owner.nycll152.ops.RouteStatusRecord
@param String currentPath
@param String canonicalUrl
@param String teamSourceCheckedAt
@param int teamSourceCount
@param boolean showTeamTrust
@param AdminSummary summary

@template.layout.page(
    currentPath = currentPath,
    pageTitle = "Admin summary",
    metaDescription = "File-backed route, lead, and checker summary for the LL152 verdict application.",
    canonicalUrl = canonicalUrl,
    noindex = true,
    teamSourceCheckedAt = teamSourceCheckedAt,
    teamSourceCount = teamSourceCount,
    showTeamTrust = showTeamTrust,
    content = @`
        <section class="band">
            <div class="shell">
                <div class="section-heading">
                    <p class="eyebrow">Admin summary</p>
                    <h1>Route health, CTA activity, and lead totals.</h1>
                    <p>Generated ${summary.generatedAt().toString()}.</p>
                </div>

                <div class="metric-grid">
                    <article class="metric">
                        <span>Total routes</span>
                        <strong>${summary.totalRoutes()}</strong>
                    </article>
                    <article class="metric">
                        <span>Indexable routes</span>
                        <strong>${summary.indexableRoutes()}</strong>
                    </article>
                    <article class="metric">
                        <span>Held routes</span>
                        <strong>${summary.heldRoutes()}</strong>
                    </article>
                    <article class="metric">
                        <span>Checker starts</span>
                        <strong>${summary.checkerStarts()}</strong>
                    </article>
                    <article class="metric">
                        <span>Checker completions</span>
                        <strong>${summary.checkerCompletions()}</strong>
                    </article>
                    <article class="metric">
                        <span>CTA clicks</span>
                        <strong>${summary.ctaClicks()}</strong>
                    </article>
                    <article class="metric">
                        <span>Lead submissions</span>
                        <strong>${summary.leadSubmissions()}</strong>
                    </article>
                </div>
            </div>
        </section>

        <section class="band band--soft">
            <div class="shell two-up">
                <div class="content-block">
                    <p class="eyebrow">Exports</p>
                    <h2>Download the raw files</h2>
                    <p>Use the raw exports when you need the full lead log, event log, route status sheet, or JSON snapshot outside the admin screen.</p>
                    <div class="command-panel__actions">
                        <a class="button" href="/admin/download/leads.csv">Leads CSV</a>
                        <a class="button button--secondary" href="/admin/download/lead-events.csv">Events CSV</a>
                        <a class="button button--secondary" href="/admin/download/route-status.csv">Route status CSV</a>
                        <a class="button button--secondary" href="/admin/download/admin-metrics-snapshot.json">Metrics JSON</a>
                    </div>
                </div>
                <div class="content-block">
                    <p class="eyebrow">Stale or blocked routes</p>
                    <h2>Needs follow-up</h2>
                    <ul class="bullet-list">
                        @for(var route : summary.staleRoutes())
                            <li>${route.routeId()} - ${route.sourceFreshnessStatus()}</li>
                        @endfor
                    </ul>
                </div>
            </div>
        </section>

        <section class="band">
            <div class="shell content-block">
                <p class="eyebrow">Promotion review</p>
                <h2>Current recommendation</h2>
                <p>${summary.promotionReview().summary()}</p>
                <ul class="bullet-list">
                    @for(var note : summary.promotionReview().heldRecommendations())
                        <li>${note}</li>
                    @endfor
                </ul>
            </div>
        </section>

        <section class="band">
            <div class="shell content-block">
                <p class="eyebrow">Top routes</p>
                <h2>Most active routes right now</h2>
                <div class="table-scroll">
                    <table>
                        <thead>
                        <tr>
                            <th>Route</th>
                            <th>Checker starts</th>
                            <th>Completions</th>
                            <th>CTA clicks</th>
                            <th>Leads</th>
                        </tr>
                        </thead>
                        <tbody>
                        @for(var route : summary.topRoutes())
                            <tr>
                                <td>${route.routeId()}</td>
                                <td>${route.checkerStarts()}</td>
                                <td>${route.checkerCompletions()}</td>
                                <td>${route.ctaClicks()}</td>
                                <td>${route.leadSubmissions()}</td>
                            </tr>
                        @endfor
                        </tbody>
                    </table>
                </div>
            </div>
        </section>

        <section class="band">
            <div class="shell two-up">
                <div class="content-block">
                    <p class="eyebrow">Recent leads</p>
                    <h2>Latest review requests</h2>
                    @if(summary.recentLeads().isEmpty())
                        <p>No leads captured yet.</p>
                    @else
                        <div class="table-scroll">
                            <table>
                                <thead>
                                <tr>
                                    <th>Created</th>
                                    <th>Route</th>
                                    <th>Email</th>
                                    <th>Building</th>
                                </tr>
                                </thead>
                                <tbody>
                                @for(var lead : summary.recentLeads())
                                    <tr>
                                        <td>${lead.createdAt().toString()}</td>
                                        <td>${lead.routeId()}</td>
                                        <td>${lead.email()}</td>
                                        <td>${lead.buildingAddress()}</td>
                                    </tr>
                                @endfor
                                </tbody>
                            </table>
                        </div>
                    @endif
                </div>
                <div class="content-block">
                    <p class="eyebrow">Recent events</p>
                    <h2>Latest CTA and intake activity</h2>
                    @if(summary.recentEvents().isEmpty())
                        <p>No lead events recorded yet.</p>
                    @else
                        <div class="table-scroll">
                            <table>
                                <thead>
                                <tr>
                                    <th>Created</th>
                                    <th>Route</th>
                                    <th>Event</th>
                                    <th>Detail</th>
                                </tr>
                                </thead>
                                <tbody>
                                @for(var event : summary.recentEvents())
                                    <tr>
                                        <td>${event.createdAt().toString()}</td>
                                        <td>${event.routeId()}</td>
                                        <td>${event.eventType()}</td>
                                        <td>${event.detail()}</td>
                                    </tr>
                                @endfor
                                </tbody>
                            </table>
                        </div>
                    @endif
                </div>
            </div>
        </section>
    `
)
