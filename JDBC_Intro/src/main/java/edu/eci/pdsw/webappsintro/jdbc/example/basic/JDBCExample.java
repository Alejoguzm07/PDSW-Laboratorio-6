/*
 * Copyright (C) 2015 hcadavid
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package edu.eci.pdsw.webappsintro.jdbc.example.basic;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author hcadavid
 */
public class JDBCExample {
    
    public static void main(String args[]){
        try {
            String url="jdbc:mysql://desarrollo.is.escuelaing.edu.co:3306/bdprueba";
            String driver="com.mysql.jdbc.Driver";
            String user="bdprueba";
            String pwd="bdprueba";
                        
            Class.forName(driver);
            Connection con=DriverManager.getConnection(url,user,pwd);
            con.setAutoCommit(false);
                 
            
            System.out.println("Valor total pedido 1:"+valorTotalPedido(con, 1));
            
            List<String> prodsPedido=nombresProductosPedido(con, 1);
            
            
            System.out.println("Productos del pedido 1:");
            System.out.println("-----------------------");
            for (String nomprod:prodsPedido){
                System.out.println(nomprod);
            }
            System.out.println("-----------------------");
            
            
            int suCodigoECI=20134423;
            registrarNuevoProducto(con, 2138459, "Yohanna Toro", 25690000);          
            registrarNuevoProducto(con, 2132219, "Alejandro Guzman", 333695244);           
            
            con.close();
                                   
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(JDBCExample.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
    }
    
    /**
     * Agregar un nuevo producto con los parámetros dados
     * @param con la conexión JDBC
     * @param codigo
     * @param nombre
     * @param precio
     * @throws SQLException 
     */
    public static void registrarNuevoProducto(Connection con, int codigo, String nombre,int precio) throws SQLException{
        //Crear preparedStatement
    	PreparedStatement registerProduct = null;
    	
    	String insertProduct=
    			"insert into ORD_PRODUCTOS (codigo,nombre,precio) values(?,?,?)";
    	//Asignar parámetros
    	try {
    		con.setAutoCommit(false);
    		registerProduct= con.prepareStatement(insertProduct);
        	registerProduct.setInt(1,codigo);
        	registerProduct.setString(2,nombre);
        	registerProduct.setInt(3,precio);
            //usar 'execute'
        	registerProduct.execute();
            }catch(SQLException e) {
            	if(con != null) {
            		try {
            			System.err.print("Transaction is being rolled back\n");
            			con.rollback();
            		}catch(SQLException ex) {
            			System.err.print(ex.getMessage());
            		}
            	}
            }finally {
            	if(registerProduct != null) {
            		registerProduct.close();
            	}
            	con.setAutoCommit(true);
            }    	      
    }
    
    /**
     * Consultar los nombres de los productos asociados a un pedido
     * @param con la conexión JDBC
     * @param codigoPedido el código del pedido
     * @return
     * @throws SQLException  
     */
    public static List<String> nombresProductosPedido(Connection con, int codigoPedido) {
        List<String> np=new LinkedList<>();
        
        //Crear prepared statement
		PreparedStatement productName = null;
		
		String consultName=
		"select OPR.nombre from ORD_PEDIDOS as OP,ORD_DETALLES_PEDIDO as ODP, ORD_PRODUCTOS as OPR where "+
		"OP.codigo=ODP.pedido_fk and ODP.producto_fk=OPR.codigo and OP.codigo=?";
		
			
			
		try {
			//asignar parámetros
			productName= con.prepareStatement(consultName);
			productName.setInt(1,codigoPedido);
			//usar executeQuery
			ResultSet result=productName.executeQuery();
			//Sacar resultados del ResultSet
			
			while (result.next()){
				//Llenar la lista y retornarla
				np.add(result.getString("OPR.nombre"));
			}
		} catch (SQLException e) {
			System.err.print(e.getMessage());
		}
		  
        return np;
    }

    
    /**
     * Calcular el costo total de un pedido
     * @param con
     * @param codigoPedido código del pedido cuyo total se calculará
     * @return el costo total del pedido (suma de: cantidades*precios)
     */
    public static int valorTotalPedido(Connection con, int codigoPedido) throws SQLException{
    	PreparedStatement valorTotal = null;
    	int valtot = 0;
    	String conultValorTotal=
    	"select SUM(OPR.precio*ODP.cantidad) as Total from ORD_PEDIDOS as OP,ORD_DETALLES_PEDIDO as ODP, ORD_PRODUCTOS as OPR where "+
		"OP.codigo=ODP.pedido_fk and ODP.producto_fk=OPR.codigo and OP.codigo=?";
    	
	    //Crear prepared statement
		//asignar parámetros
		try {
			valorTotal= con.prepareStatement(conultValorTotal);
			valorTotal.setInt(1,codigoPedido);
			//usar executeQuery
			ResultSet result=valorTotal.executeQuery();
			//Sacar resultados del ResultSet
			
			while (result.next()){
				//Llenar la lista y retornarla
				//String nombreProducto=result.getString("OPR.nombre");
				valtot = result.getInt("Total");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.err.print(e.getMessage());
		}
	
    return valtot;
    }
    

    
    
    
}
