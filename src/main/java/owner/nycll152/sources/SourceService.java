package owner.nycll152.sources;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SourceService {

    private final SourceRepository sourceRepository;

    public SourceService(SourceRepository sourceRepository) {
        this.sourceRepository = sourceRepository;
    }

    public List<SourceRecord> findByIds(List<String> sourceIds) {
        return sourceRepository.findByIds(sourceIds);
    }

    public List<OfficialSourceLink> officialLinksFor(List<String> sourceIds) {
        return findByIds(sourceIds).stream()
                .map(source -> new OfficialSourceLink(source.sourceId(), source.title(), source.url()))
                .toList();
    }
}
