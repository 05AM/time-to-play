package org.example.batch.job.notification;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "wishlist-sale-mail")
public record WishlistSaleMailProperties(
    String cron,
    Integer lookbackHours,
    Integer chunkSize,
    Integer fetchSize,
    String wishlistUrl,
    String fromEmail,
    String subjectPrefix
) {}
