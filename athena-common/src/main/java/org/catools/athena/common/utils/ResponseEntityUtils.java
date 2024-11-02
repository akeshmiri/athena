package org.catools.athena.common.utils;

import lombok.experimental.UtilityClass;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.Set;


@UtilityClass
public class ResponseEntityUtils {

  public static final String ENTITY_ID = "entity_id";

  public static <T> ResponseEntity<Set<T>> okOrNoContent(final Set<T> t) {
    return t.isEmpty() ? noContent() : ok(t);
  }

  public static <T> ResponseEntity<T> okOrNoContent(final Optional<T> t) {
    return t.map(ResponseEntityUtils::ok).orElseGet(ResponseEntityUtils::noContent);
  }

  public static <T> ResponseEntity<T> noContent() {
    return ResponseEntity.noContent().build();
  }

  public static <T> ResponseEntity<T> created(final String path, final Long id) {
    return ResponseEntity.created(getLocation(path, id))
        .header(ENTITY_ID, String.valueOf(id))
        .build();
  }

  public static <T> ResponseEntity<T> ok(final T t) {
    return ResponseEntity.ok().body(t);
  }

  public static <T> ResponseEntity<T> alreadyReported(final String path, final Long id) {
    return ResponseEntity
        .status(HttpStatus.ALREADY_REPORTED)
        .location(getLocation(path, id))
        .header(ENTITY_ID, String.valueOf(id))
        .build();
  }

  public static <T> ResponseEntity<T> updated(final String path, final Long id) {
    return ResponseEntity
        .status(HttpStatus.OK)
        .location(getLocation(path, id))
        .header(ENTITY_ID, String.valueOf(id))
        .build();
  }

  public static ResponseEntity<Void> conflicted() {
    return ResponseEntity
        .status(HttpStatus.CONFLICT)
        .build();
  }

  public static Long getId(final URI location) {
    String[] parts = location.getPath().split("/");
    return Long.valueOf(parts[parts.length - 1]);
  }

  public static Long getEntityId(final ResponseEntity<?> response) {
    return getEntityId(response.getHeaders());
  }

  public static Long getEntityId(final HttpHeaders headers) {
    List<String> header = headers.get(ENTITY_ID);
    return (header == null || header.isEmpty()) ? null : Long.valueOf(header.get(0));
  }

  private static URI getLocation(final String path, final Long id) {
    return UriComponentsBuilder
        .fromPath(path)
        .pathSegment(String.valueOf(id))
        .build()
        .toUri();
  }
}
