/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package adapter;

import org.apache.logging.log4j.Logger;
import utils.InfoViewLogAppender;

/**
 *
 * @author adrest18
 */
interface ILog4jAdapter {
    
    public Logger getLogger(String loggerName);
    public InfoViewLogAppender getInfoViewLogAppender();
    
}
