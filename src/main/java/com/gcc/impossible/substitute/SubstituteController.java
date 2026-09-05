package com.gcc.impossible.substitute;

import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/stores")
public class SubstituteController {

    private final SubstituteCandidateService substituteCandidateService;

    public SubstituteController(SubstituteCandidateService substituteCandidateService) {
        this.substituteCandidateService = substituteCandidateService;
    }

    @GetMapping("/{regno}/substitutes")
    public List<SubstituteDto> findCandidates(@PathVariable String regno) {
        return substituteCandidateService.findCandidates(regno);
    }
}
