/*
 * Copyright (C) 2014 SimElectricity
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301
 * USA
 */

package simElectricity.API.EnergyTile;


import net.minecraft.util.EnumFacing;

/**
 * This interface represents a transformer
 * <p/>
 * See wiki for details of the model of transformer, to enhance the understanding 0w0
 */
public interface ITransformer {
    /**
     * Return the facing of the primary
     */
    EnumFacing getPrimarySide();

    /**
     * Return the facing of the secondary
     */
    EnumFacing getSecondarySide();

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
    double getResistance();

    /**
     * Return the primary-secondary ratio, >1 for step up, <1for step down
     */
    double getRatio();
    
    /**
     * This class represents the primary of a transformer
     * <p/>
     * You can Override this class when necessary!
     */
    public static class Primary implements ITransformerWinding {
        
        private ITransformer core;

        public Primary(ITransformer core) {
            this.core = core;
        }
        
        /**
         * Usually do not alternate this
         */
        @Override
        public double getResistance() {
            return core.getResistance();
        }

        /**
         * Usually do not alternate this
         */
        @Override
        public double getRatio() {
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
        double getRatio();

        boolean isPrimary();

        ITransformer getCore();
    }
}
