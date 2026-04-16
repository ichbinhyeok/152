@import java.util.List
@import owner.nycll152.pages.RoutePage
@import owner.nycll152.pages.RouteEnhancement
@import owner.nycll152.pages.Section
@import owner.nycll152.sources.SourceRecord
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
@param RouteEnhancement routeEnhancement
@param List<SourceRecord> officialSources

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
        <section class="directive-hero ${"trust".equals(page.family()) ? "directive-hero--trust" : ""}" style="--hero-image:url('${page.imageUrl()}');">
            <div class="directive-hero__meta">
                <div>
                    <p class="eyebrow">${page.eyebrow()}</p>
                    <div class="breadcrumb-row">
                        <a href="/">Home</a>
                        <span>/</span>
                        <a href="/ll152-checker/">Checker</a>
                        <span>/</span>
                        <span>${page.id()}</span>
                    </div>
                </div>
                <div class="document-meta">
                    <span>Route ID</span>
                    <strong>${page.id()}</strong>
                    <span>Source check ${teamSourceCheckedAt.isBlank() ? "current" : teamSourceCheckedAt}</span>
                </div>
            </div>
            <h1>${page.title()}</h1>
            <p class="directive-hero__summary">${page.hero()}</p>
        </section>

        <section class="directive-layout ${"trust".equals(page.family()) ? "directive-layout--trust" : ""}">
            <div class="directive-canvas">
                @if(!page.sections().isEmpty())
                    <div class="section-matrix">
                        @for(var section : page.sections())
                            <article class="protocol-block">
                                <div class="protocol-block__header">
                                    <p class="mono-label">Protocol section</p>
                                    <h2>${section.title()}</h2>
                                </div>
                                @for(var paragraph : section.paragraphs())
                                    <p>${paragraph}</p>
                                @endfor
                                @if(!section.bullets().isEmpty())
                                    <ul class="bullet-list">
                                        @for(var bullet : section.bullets())
                                            <li>${bullet}</li>
                                        @endfor
                                    </ul>
                                @endif
                            </article>
                        @endfor
                    </div>
                @endif

                @if(!routeEnhancement.nextActionChecklist().isEmpty())
                    <article class="timeline-panel">
                        <div class="panel-heading">
                            <p class="mono-label">Required sequence</p>
                            <h2>What to prepare next</h2>
                        </div>
                        <ol class="timeline-list">
                            @for(var item : routeEnhancement.nextActionChecklist())
                                <li>${item}</li>
                            @endfor
                        </ol>
                    </article>
                @endif

                @if(!routeEnhancement.escalationTriggers().isEmpty())
                    <article class="protocol-summary">
                        <div class="panel-heading">
                            <p class="mono-label">Decision boundary</p>
                            <h2>Escalate before you continue when</h2>
                        </div>
                        <ul class="bullet-list">
                            @for(var item : routeEnhancement.escalationTriggers())
                                <li>${item}</li>
                            @endfor
                        </ul>
                    </article>
                @endif

                <article class="protocol-summary">
                    <div class="panel-heading">
                        <p class="mono-label">Decision basis</p>
                        <h2>Why this route exists</h2>
                    </div>
                    <p>${page.summary()}</p>
                    <div class="protocol-summary__grid">
                        <div>
                            <span class="mono-label">Route ID</span>
                            <strong>${page.id()}</strong>
                        </div>
                        <div>
                            <span class="mono-label">Family</span>
                            <strong>${page.family()}</strong>
                        </div>
                        <div>
                            <span class="mono-label">Phase</span>
                            <strong>${page.phase() == null || page.phase().isBlank() ? "route" : page.phase()}</strong>
                        </div>
                    </div>
                </article>
            </div>

            <aside class="action-column">
                <article class="action-panel ${"trust".equals(page.family()) ? "action-panel--quiet" : "action-panel--dark"}">
                    <p class="mono-label ${"trust".equals(page.family()) ? "" : "mono-label--light"}">${"trust".equals(page.family()) ? "Using this page" : "When to contact now"}</p>
                    <h2>${"trust".equals(page.family()) ? "Read the boundary first, then share only what changes the route." : "Contact now when one missing fact still changes the filing path."}</h2>
                    <p>${page.summary()}</p>
                    <p>${"trust".equals(page.family()) ? "These trust pages explain who operates the site, how the intake works, and what should stay out of the form." : "Send the case details when the blocker is still a routing question, a due-window question, or a gas-status question."}</p>
                    <p>${"trust".equals(page.family()) ? "Contact: shinhyeok22@gmail.com" : "The intake keeps the route, blocker, and building reference together so the next reply starts from the same case context."}</p>
                    <p>Source check: ${teamSourceCheckedAt}. Current page basis: ${teamSourceCount} official references.</p>
                    <button class="button ${"trust".equals(page.family()) ? "" : "button--inverse"}" type="button" data-lead-open data-route-id="${page.id()}" data-route-path="${page.path()}" data-intent="${page.ctaIntent()}">Send case details</button>
                    <a class="ghost-link ${"trust".equals(page.family()) ? "" : "ghost-link--light"}" href="/ll152-checker/">Run the checker first</a>
                </article>

                <article class="action-panel">
                    <p class="mono-label">${"trust".equals(page.family()) ? "Questions about the intake" : "What to send if you contact now"}</p>
                    <h2>${"trust".equals(page.family()) ? "Keep the intake narrow and specific." : "Share the facts that still change the next step."}</h2>
                    <ul class="checklist-list">
                        <li>The likely LL152 branch still controlling this building.</li>
                        <li>The one missing document or fact still blocking the next step.</li>
                        <li>A building reference, gas status, or due-window clue when you have it.</li>
                    </ul>
                </article>

                @if(!officialSources.isEmpty())
                    <article class="action-panel">
                        <p class="mono-label">Official source anchors</p>
                        <ul class="source-list">
                            @for(var source : officialSources)
                                <li>
                                    <a href="${source.url()}" target="_blank" rel="noopener">${source.title()}</a>
                                    <span>Verified ${source.verifiedAt()}</span>
                                </li>
                            @endfor
                        </ul>
                    </article>
                @endif

                <figure class="reference-figure">
                    <img src="${page.imageUrl()}" alt="${page.imageAlt()}">
                </figure>
            </aside>
        </section>

        @if(!relatedRoutes.isEmpty())
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
        @endif
    `
)
