package se.sundsvall.notes.api.model;

import static io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY;

import java.util.Objects;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Metadata model")
public class MetaData {

	@Schema(description = "Current page", example = "5", accessMode = READ_ONLY)
	private int page;

	@Schema(description = "Displayed objects per page", example = "20", accessMode = READ_ONLY)
	private int limit;

	@Schema(description = "Displayed objects on current page", example = "13", accessMode = READ_ONLY)
	private int count;

	@Schema(description = "Total amount of hits based on provided search parameters", example = "98", accessMode = READ_ONLY)
	private long totalRecords;

	@Schema(description = "Total amount of pages based on provided search parameters", example = "23", accessMode = READ_ONLY)
	private int totalPages;

	public static MetaData create() {
		return new MetaData();
	}

	public long getTotalRecords() {
		return totalRecords;
	}

	public void setTotalRecords(long totalRecords) {
		this.totalRecords = totalRecords;
	}

	public MetaData withTotalRecords(long totalRecords) {
		this.totalRecords = totalRecords;
		return this;
	}

	public int getTotalPages() {
		return totalPages;
	}

	public void setTotalPages(int totalPages) {
		this.totalPages = totalPages;
	}

	public MetaData withTotalPages(int totalPages) {
		this.totalPages = totalPages;
		return this;
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public MetaData withPage(int page) {
		this.page = page;
		return this;
	}

	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

	public MetaData withLimit(int limit) {
		this.limit = limit;
		return this;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public MetaData withCount(int count) {
		this.count = count;
		return this;
	}

	@Override
	public int hashCode() {
		return Objects.hash(count, limit, page, totalPages, totalRecords);
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
		MetaData other = (MetaData) obj;
		return count == other.count && limit == other.limit && page == other.page && totalPages == other.totalPages && totalRecords == other.totalRecords;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("MetaData [page=").append(page).append(", limit=").append(limit).append(", count=").append(count).append(", totalRecords=").append(totalRecords).append(", totalPages=").append(totalPages).append("]");
		return builder.toString();
	}

}
