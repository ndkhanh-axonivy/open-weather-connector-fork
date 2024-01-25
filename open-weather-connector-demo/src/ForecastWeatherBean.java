import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

@ManagedBean
@ViewScoped
public class ForecastWeatherBean implements Serializable {

	private static final long serialVersionUID = 4700043015312367231L;

	private Date selectedTime;
	private TypeOfDegree selectedTypeOfDegree = TypeOfDegree.C;
	private String weatherDegree;
	private String weatherDetail;
	private String humidityNumber;
	private String windSpeed;
	private String formatted12HourTime;
	private String formattedDateTime;

	@PostConstruct
	private void init() {
		
	}
	
	public String getFormatted12HourTime() {
		return formatted12HourTime;
	}

	public String getFormattedDateTime() {
		return formattedDateTime;
	}

	public void setSelectedTime(Date selectedTime) {
		this.selectedTime = selectedTime;
		convertTimeToDateFormat();
		convertTimeTo12HourFormat();
	}

	public void convertTimeToDateFormat() {
		if (selectedTime == null) {
			formattedDateTime = "Invalid time";
			return;
		}

		SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMMM yyyy");
		formattedDateTime = sdf.format(selectedTime);
	}

	public void convertTimeTo12HourFormat() {
		if (selectedTime == null) {
			formattedDateTime = "Invalid time";
			return;
		}

		SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
		formattedDateTime = sdf.format(selectedTime);
	}

	public String getWeatherDegree() {
		return weatherDegree;
	}
	
	public String getWeatherDetail() {
		return weatherDetail;
	}

	public TypeOfDegree isSelectedTypeOfDegree() {
		return selectedTypeOfDegree;
	}

	public void setSelectedTypeOfDegree(TypeOfDegree selectedTypeOfDegree) {
		this.selectedTypeOfDegree = selectedTypeOfDegree;
	}
	
	public String getHumidityNumber() {
		return humidityNumber;
	}

	public String getWindSpeed() {
		return windSpeed;
	}
}
