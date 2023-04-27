package se.sundsvall.notes.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import se.sundsvall.dept44.common.validators.annotation.ValidMunicipalityId;
import se.sundsvall.dept44.common.validators.annotation.ValidUuid;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import java.util.Objects;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;
import static java.lang.Integer.parseInt;

@Schema(description = "FindNotesRequest model")
public class FindNotesRequest {
	private static final String DEFAULT_PAGE = "1";
	private static final String DEFAULT_LIMIT = "100";

	@Schema(description = "Context for note", example = "SUPPORT")
	private String context;

	@Schema(description = "Role of note creator", example = "FIRST_LINE_SUPPORT")
	private String role;

	@Schema(description = "Id of the client who is the owner of the note", example = "SUPPORT_MGMT")
	private String clientId;

	@Schema(description = "Party id (e.g. a personId or an organizationId)", example = "81471222-5798-11e9-ae24-57fa13b361e1")
	@ValidUuid(nullable = true)
	private String partyId;

	@Schema(description = "Id for the case", example = "12345")
	private String caseId;

	@Schema(description = "Municipality id for the case", example = "2229", requiredMode = REQUIRED)
	@Size(min = 1, max = 255)
	@ValidMunicipalityId
	private String municipalityId;

	@Schema(description = "Page number", example = DEFAULT_PAGE, defaultValue = DEFAULT_PAGE)
	@Min(1)
	protected int page = parseInt(DEFAULT_PAGE);

	@Schema(description = "Result size per page", example = DEFAULT_LIMIT, defaultValue = DEFAULT_LIMIT)
	@Min(1)
	@Max(1000)
	protected int limit = parseInt(DEFAULT_LIMIT);
	
	public static FindNotesRequest create() {
		return new FindNotesRequest();
	}

	public String getContext() {
		return context;
	}

	public void setContext(String context) {
		this.context = context;
	}

	public FindNotesRequest withContext(String context) {
		this.context = context;
		return this;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public FindNotesRequest withRole(String role) {
		this.role = role;
		return this;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public FindNotesRequest withClientId(String clientId) {
		this.clientId = clientId;
		return this;
	}

	public String getPartyId() {
		return partyId;
	}

	public void setPartyId(String partyId) {
		this.partyId = partyId;
	}

	public FindNotesRequest withPartyId(String partyId) {
		this.partyId = partyId;
		return this;
	}

	public String getCaseId() {
		return caseId;
	}

	public void setCaseId(String caseId) {
		this.caseId = caseId;
	}

	public FindNotesRequest withCaseId(String caseId) {
		this.caseId = caseId;
		return this;
	}

	public String getMunicipalityId() {
		return municipalityId;
	}

	public void setMunicipalityId(String municipalityId) {
		this.municipalityId = municipalityId;
	}

	public FindNotesRequest withMunicipalityId(String municipalityId) {
		this.municipalityId = municipalityId;
		return this;
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public FindNotesRequest withPage(int page) {
		this.page = page;
		return this;
	}

	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

	public FindNotesRequest withLimit(int limit) {
		this.limit = limit;
		return this;
	}

	@Override
	public int hashCode() {
		return Objects.hash(caseId, clientId, context, limit, page, partyId, role, municipalityId);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		FindNotesRequest other = (FindNotesRequest) obj;
		return Objects.equals(caseId, other.caseId) && Objects.equals(clientId, other.clientId) && Objects.equals(context, other.context) && limit == other.limit && page == other.page && Objects.equals(partyId, other.partyId) && Objects.equals(role,
			other.role) && Objects.equals(municipalityId, other.municipalityId);
	}

	@Override
	public String toString() {
		return new StringBuilder().append("FindNotesRequest [context=").append(context)
			.append(", role=").append(role)
			.append(", clientId=").append(clientId)
			.append(", partyId=").append(partyId)
			.append(", caseId=").append(caseId)
			.append(", page=").append(page)
			.append(", limit=").append(limit)
			.append(", municipalityId=").append(municipalityId)
			.append("]").toString();
	}
}
