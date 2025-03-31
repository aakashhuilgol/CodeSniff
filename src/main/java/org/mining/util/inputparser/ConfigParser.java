package org.mining.util.inputparser;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class ConfigParser {
    public static CodeAnalysisConfig parseConfig(InputStream input) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(input, CodeAnalysisConfig.class);
    }
}
