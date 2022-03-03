package com.turboparser.turbo.service;

import com.turboparser.turbo.constant.Language;

public interface MessageProvider {

    String getMessage(String message, Language lang);

}
