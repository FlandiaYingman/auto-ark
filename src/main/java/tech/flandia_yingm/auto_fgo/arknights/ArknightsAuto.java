package tech.flandia_yingm.auto_fgo.arknights;

import lombok.NonNull;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import tech.flandia_yingm.auto_fgo.device.Device;

@Slf4j
@ToString(onlyExplicitlyIncluded = true)
public class ArknightsAuto {

    @NonNull
    @ToString.Include
    private final Device dev;

    public enum 理智恢复策略 {
        不恢复,
        使用合剂恢复,
        使用原石恢复
    }


    public ArknightsAuto(Device dev) {
        this.dev = dev;
    }

    public void 代理指挥(理智恢复策略 策略) {
        log.info("{} - ", this);

        dev.tillMatched(明日方舟模板.任务开始);
        dev.tap(明日方舟坐标.开始任务);

        dev.tillMatched(明日方舟模板.任务开始确认, 明日方舟模板.理智不足, 明日方舟模板.理智不足且无合剂);
        val 理智不足状态 = dev.matches(明日方舟模板.理智不足, 明日方舟模板.理智不足且无合剂);
        if (理智) {
            if ((策略 == 理智恢复策略.不恢复) ||
                (策略 == 理智恢复策略.使用合剂恢复 && 理智不足状态 == 明日方舟模板.理智不足且无合剂)) {
                return;
            }
            if (理智不足状态 == 明日方舟模板.理智不足 && 策略 == 理智恢复策略.使用原石恢复) {
                dev.tap(明日方舟坐标.选择原石恢复理智);
                dev.delay(1000);
            }
            dev.tap(明日方舟坐标.确认恢复理智);
            dev.delay(1000);
            dev.tillMatched(明日方舟模板.任务开始);
            dev.tap(明日方舟坐标.开始任务);
            dev.tillMatched(明日方舟模板.任务开始确认);
        }

        dev.tap(明日方舟坐标.确认开始任务);
        dev.tillMatched(明日方舟模板.任务完成, 明日方舟模板.任务失败, 明日方舟模板.等级提升);

        if (dev.matches(明日方舟模板.等级提升)) {
            dev.tap(明日方舟坐标.完成任务);
            dev.tillMatched(明日方舟模板.任务完成, 明日方舟模板.任务失败, 明日方舟模板.等级提升);
        }

        dev.tap(明日方舟坐标.完成任务);
    }

}
