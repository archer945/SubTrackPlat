package com.defectmanager.service;

import com.defectmanager.query.DefectQuery;
import jakarta.servlet.http.HttpServletResponse;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

public interface exportService {

     ByteArrayOutputStream exportDefectsToCsv(DefectQuery query, HttpServletResponse response) throws IOException;
}