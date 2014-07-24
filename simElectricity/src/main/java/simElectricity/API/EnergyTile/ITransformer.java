package simElectricity.API.EnergyTile;

import net.minecraftforge.common.util.ForgeDirection;

/**
 * This interface represents a transformer
 * 
 * See wiki for details of the model of transformer, to enhance the understanding 0w0
 */
public interface ITransformer {
    /**
     * Return the facing of the primary
     */
    ForgeDirection getPrimarySide();

    /**
     * Return the facing of the secondary
     */
    ForgeDirection getSecondarySide();

    /**
     * Return the instance of the primary
     */
    ITransformerWinding getPrimary();

    /**
     * Return the instance of the secondary
     */
    ITransformerWinding getSecondary();

    /**
     * Return the output resistance of the transformer, must not be zero!
     */
    float getResistance();

    /**
     * Return the primary-secondary ratio, >1 for step up, <1for step down
     */
    float getRatio();


    /**
     * This class represents the primary of a transformer
     */
    public static class Primary implements ITransformerWinding {
        /**
         * You can Override this class when necessary!
         */
        private ITransformer core;

        public Primary(ITransformer core) {
            this.core = core;
        }

        /**
         * Usually do not alternate this
         */
        @Override
        public float getResistance() {
            return core.getResistance();
        }

        /**
         * Usually do not alternate this
         */
        @Override
        public float getRatio() {
            return core.getRatio();
        }

        /**
         * Usually do not alternate this
         */
        @Override
        public boolean isPrimary() {
            return true;
        }

        /**
         * Usually do not alternate this
         */
        @Override
        public ITransformer getCore() {
            return core;
        }
    }

    /**
     * This class represents the secondary of a transformer , do not alternate this class!
     */
    public static class Secondary extends Primary {
        public Secondary(ITransformer core) {
            super(core);
        }

        @Override
        public boolean isPrimary() {
            return false;
        }
    }

    /**
     * This class represents either primary or secondary of a transformer, Usually internal uses only! So don't worry about this 0_0
     */
    public interface ITransformerWinding extends IBaseComponent {
        float getRatio();

        boolean isPrimary();

        ITransformer getCore();
    }
}
