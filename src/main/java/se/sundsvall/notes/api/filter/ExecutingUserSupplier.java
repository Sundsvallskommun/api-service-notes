package se.sundsvall.notes.api.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static java.util.Optional.ofNullable;

@Component
public class ExecutingUserSupplier extends OncePerRequestFilter {
	private static final String UNKNOWN = "UNKNOWN";

	public static final String AD_USER_HEADER_KEY = "sentbyuser";

	private static final ThreadLocal<String> THREAD_LOCAL_AD_USER = new ThreadLocal<>();

	public String getAdUser() {
		return THREAD_LOCAL_AD_USER.get();
	}

	@Override
	protected void doFilterInternal(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
		// Extract AD-user from ad-user header
		THREAD_LOCAL_AD_USER.set(extractAdUser(request));

		try {
			filterChain.doFilter(request, response);
		} finally {
			// Remove value from threadlocal when filter chain has been processed
			THREAD_LOCAL_AD_USER.remove();
		}
	}

	String extractAdUser(HttpServletRequest request) {
		return ofNullable(request.getHeader(AD_USER_HEADER_KEY))
			.filter(StringUtils::hasText)
			.orElse(UNKNOWN);
	}
}
