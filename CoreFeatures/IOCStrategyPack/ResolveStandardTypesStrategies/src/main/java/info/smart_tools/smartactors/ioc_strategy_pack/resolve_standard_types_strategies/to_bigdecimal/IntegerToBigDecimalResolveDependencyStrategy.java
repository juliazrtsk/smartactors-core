package info.smart_tools.smartactors.ioc_strategy_pack.resolve_standard_types_strategies.to_bigdecimal;

import info.smart_tools.smartactors.base.interfaces.iresolve_dependency_strategy.IResolveDependencyStrategy;
import info.smart_tools.smartactors.base.interfaces.iresolve_dependency_strategy.exception.ResolveDependencyStrategyException;

import java.math.BigDecimal;
/**
 * from integer to bigdecimal
 */
public class IntegerToBigDecimalResolveDependencyStrategy implements IResolveDependencyStrategy {
    @Override
    public <T> T resolve(final Object... args) throws ResolveDependencyStrategyException {
        try {
            return (T) BigDecimal.valueOf((Integer) args[0]);
        } catch (Exception e) {
            throw new ResolveDependencyStrategyException(e);
        }
    }
}
