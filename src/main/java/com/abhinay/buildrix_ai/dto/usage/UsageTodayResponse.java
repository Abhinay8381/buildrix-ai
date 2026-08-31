package com.abhinay.buildrix_ai.dto.usage;

public record UsageTodayResponse(
        Integer tokensUsed,
        Integer tokensRemaining,
        Integer previewsRunning,
        Integer previewsLimit
) {
}
