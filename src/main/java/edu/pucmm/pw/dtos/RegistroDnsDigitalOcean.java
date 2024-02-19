package edu.pucmm.pw.dtos;

/**
 * Record sobre el uso de los
 * @param id
 * @param type
 * @param name
 * @param data
 * @param priority
 * @param port
 * @param ttl
 * @param weight
 * @param flags
 */
public record RegistroDnsDigitalOcean(Long id,
                                      String type,
                                      String name,
                                      String data,
                                      int priority,
                                      int port,
                                      int ttl,
                                      int weight,
                                      int flags) {
}
