package org.example.batch.infra.mail;

import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Locale;
import java.util.Map;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class MailTemplateRenderer {

    private final TemplateEngine templateEngine;

    public String renderWishlistSale(Map<String, Object> model) {
        Context ctx = new Context(Locale.KOREA);
        ctx.setVariables(model);
        return templateEngine.process("mail/wishlist-sale", ctx);
    }
}
