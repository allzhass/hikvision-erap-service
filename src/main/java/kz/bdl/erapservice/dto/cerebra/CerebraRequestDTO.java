package kz.bdl.erapservice.dto.cerebra;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Cerebra Request to send Erap")
public class CerebraRequestDTO {
    @Schema(description = "Время отправки", example = "2024-04-02T12:00:00Z")
    private Instant sendTime;

    @Schema(description = "Интервал загрузки изображений", example = "10s")
    private String picUploadInterval;

    @Schema(description = "ID канала", example = "1")
    private Integer channelID;

    @Schema(description = "Название канала", example = "Канал 1")
    private String channelName;

    @Schema(description = "Тип данных", example = "alarm")
    private String dataType;

    @Schema(description = "Дата и время события", example = "2024-04-02T11:59:50Z")
    private Instant dateTime;

    @Schema(description = "Описание события", example = "Обнаружен автомобиль")
    private String eventDescription;

    @Schema(description = "Тип события", example = "vehicleDetection")
    private String eventType;

    @Schema(description = "IP-адрес устройства", example = "192.168.1.10")
    private String ipAddress;

    @Schema(description = "Номер порта", example = "8080")
    private Integer portNo;

    @Schema(description = "Время получения", example = "2024-04-02T12:00:01Z")
    private Instant recvTime;

    @Schema(description = "Результаты тревоги по транспортному средству")
    private List<VehicleAlarmResultDto> vehicleAlarmResult;
}
