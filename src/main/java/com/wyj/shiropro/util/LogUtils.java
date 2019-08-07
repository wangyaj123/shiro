package com.wyj.shiropro.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

/**
 * @author wangyajing
 * @date 2019-08-07
 */
public class LogUtils {

  /**
   * @param clazz 日志发出的类
   * @return
   * @description：
   */
  public static Logger get(Class<?> clazz) {
    return LoggerFactory.getLogger(clazz);
  }


  public static Logger get() {
    StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
    return LoggerFactory.getLogger(stackTrace[2].getClassName());
  }
  /**
   * @param log 日志对象
   * @param format 格式文本，{} 代表变量
   * @param arguments
   * @description：
   *     Trace等级日志，小于Debug
   */
  public static void trace(Logger log, String format, Object... arguments) {
      log.trace(format, arguments);
  }

  /**
   * @param log 日志对象
   * @param format 格式文本，{} 代表变量
   * @param arguments
   * @description： Debug等级日志，小于Info
   */
  public static void debug(Logger log, String format, Object... arguments) {
      log.debug(format, arguments);
  }

  /**
   * @param log 日志对象
   * @param format 格式文本，{} 代表变量
   * @param arguments
   * @description： info信息记录
   */
  public static void info(Logger log, String format, Object... arguments) {
      log.info(format, arguments);
  }

  /**
   * @param log 日志对象
   * @param format 格式文本，{} 代表变量
   * @param arguments 变
   * @description  warn等级日志，小于Error
   */
  public static void warn(Logger log, String format, Object... arguments) {
      log.warn(format, arguments);
  }

  /**
   * @param log 　　    日志对象
   * @param e 　　　　　需在日志中堆栈打印的异常
   * @param format 　　格式文本，{} 代表变量
   * @param arguments 　　
   * @description：    调用日志 warn信息记录
   *  每一个会话，唯一标示UUID串联所有日志
   */
  public static void warn(Logger log, Throwable e, String format, Object... arguments) {
      log.warn(format(format, arguments), e);
  }

  /**
   * @param log 日志对象
   * @param format 格式文本，{} 代表变量
   * @param arguments 变量对应的参数 @MethodName：error @ReturnType：void
   */
  public static void error(Logger log, String format, Object... arguments) {
      log.error(format, arguments);
  }

  /**
   * @param log 日志对象
   * @param e 需在日志中堆栈打印的异常
   * @param format 格式文本，{} 代表变量
   * @param arguments
   */
  public static void error(Logger log, Throwable e, String format, Object... arguments) {
      log.error(format(format, arguments), e);
  }

  /**
   * @param template 文本模板，被替换的部分用 {} 表示
   * @param values 参数值
   * @return 格式化后的文本
   */
  private static String format(String template, Object... values) {
    return String.format(template.replace("{}", "%s"), values);
  }

  /**
   * @return @MethodName：get32UUID @ReturnType：String
   * @description： 获取32位UUID
   */
  public static String get32UUID() {
    UUID uuid = UUID.randomUUID();
    String returnValue = uuid.toString();
    returnValue = returnValue.replace("-", "");
    return returnValue;
  }


  }



