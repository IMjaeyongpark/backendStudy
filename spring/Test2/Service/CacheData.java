package jaeyong.Test2.Service;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CacheData {
    private String value;

    private LocalDateTime expirationDate;
}
