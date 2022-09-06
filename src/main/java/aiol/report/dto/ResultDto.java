package aiol.report.dto;

import java.util.Objects;

public class ResultDto {
	
	private String date;
	
	private String sampleId;
	
	private String assay;
	
	private String concentration;
	
	private String unit;

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getSampleId() {
		return sampleId;
	}

	public void setSampleId(String sampleId) {
		this.sampleId = sampleId;
	}

	public String getAssay() {
		return assay;
	}

	public void setAssay(String assay) {
		this.assay = assay;
	}

	public String getConcentration() {
		return concentration;
	}

	public void setConcentration(String concentration) {
		this.concentration = concentration;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	@Override
	public int hashCode() {
		return Objects.hash(assay, concentration, date, sampleId, unit);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ResultDto other = (ResultDto) obj;
		return Objects.equals(assay, other.assay) && Objects.equals(concentration, other.concentration)
				&& Objects.equals(date, other.date) && Objects.equals(sampleId, other.sampleId)
				&& Objects.equals(unit, other.unit);
	}

	@Override
	public String toString() {
		return "ResultDto [date=" + date + ", sampleId=" + sampleId + ", assay=" + assay + ", concentration="
				+ concentration + ", unit=" + unit + "]";
	}

}
