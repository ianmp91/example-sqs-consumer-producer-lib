package com.example.sqslib.service;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import org.springframework.stereotype.Service;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author ian.paris
 * @since 2025-12-19
 */
@Service
public class XmlService {
    // Cache para no recrear el contexto cada vez (Mejora de rendimiento crítica)
    private final Map<Class<?>, JAXBContext> contextCache = new ConcurrentHashMap<>();

    /**
     * Convierte un objeto generado por XJC (IATA/AIDX) a String XML.
     */
    public <T> String toXml(T object) {
        try {
            JAXBContext context = getContext(object.getClass());
            Marshaller marshaller = context.createMarshaller();

            // Formatear bonito (true) o compacto para ahorrar bytes en SQS (false)
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, false);
            // Omitir la declaración <?xml ... ?> si prefieres payload limpio
            marshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);

            StringWriter writer = new StringWriter();
            marshaller.marshal(object, writer);
            return writer.toString();
        } catch (JAXBException e) {
            throw new RuntimeException("Error serializando objeto IATA a XML", e);
        }
    }

    /**
     * Convierte un String XML a un objeto Java (IATA/AIDX).
     */
    @SuppressWarnings("unchecked")
    public <T> T fromXml(String xml, Class<T> type) {
        try {
            JAXBContext context = getContext(type);
            Unmarshaller unmarshaller = context.createUnmarshaller();

            StringReader reader = new StringReader(xml);
            return (T) unmarshaller.unmarshal(reader);
        } catch (JAXBException e) {
            throw new RuntimeException("Error deserializando XML a objeto IATA", e);
        }
    }

    // Helper para obtener o crear el contexto (Thread-safe)
    private JAXBContext getContext(Class<?> type) throws JAXBException {
        // Usamos computeIfAbsent para crear el contexto solo si no existe en el mapa
        return contextCache.computeIfAbsent(type, clazz -> {
            try {
                return JAXBContext.newInstance(clazz);
            } catch (JAXBException e) {
                throw new RuntimeException("No se pudo crear JAXBContext para " + clazz.getName(), e);
            }
        });
    }
}
