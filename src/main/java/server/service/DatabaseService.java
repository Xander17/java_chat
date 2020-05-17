package server.service;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Getter
public class DatabaseService {
    private final Database database;
    private final ChatHistory chatHistory;
    private final AuthService authService;
    private final BlacklistService blacklistService;
}
