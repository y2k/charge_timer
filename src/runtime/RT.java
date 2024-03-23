package y2k;

import java.util.concurrent.Callable;

public class RT {

  public static String str(Object... args) {
    if (args.length == 1) {
      return args[0].toString();
    }
    StringBuilder sb = new StringBuilder();
    for (Object arg : args) {
      sb.append(arg);
    }
    return sb.toString();
  }

  /**
   * @noinspection unchecked
   */
  public static <T> T get(Object source, Object key) {
    if (source instanceof java.util.Map) {
      return (T) ((java.util.Map<?, ?>) source).get(key);
    }
    if (source instanceof java.util.List) {
      return (T) ((java.util.List<?>) source).get((Integer) key);
    }
    throw new RuntimeException("Unsupported source: " + source);
  }

  @SuppressWarnings("unchecked")
  private static <T extends Throwable> void throwException(
    Throwable exception,
    Object dummy
  ) throws T {
    throw (T) exception;
  }

  public static <T> T try_(Callable<T> f) {
    try {
      return f.call();
    } catch (Exception e) {
      RT.throwException(e, null);
      return null;
    }
  }
}
