/*
 *  Copyright (c) 2008 Boulder Community Foundation - iVolunteer
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in
 *  all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *  THE SOFTWARE.
 */

package etl;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.NoResultException;
import java.util.List;

/**
 * 
 * @author Dave Angulo
 */
@Stateless
public class locationencoderSessionBean implements locationencoderSessionLocal {
	@PersistenceContext
	private EntityManager	em;

	public void updateLocationTableLatLon() {
        Query locationQuery = em.createNamedQuery("Location.findNullLatLon");
//        Query updateQuery = em.createNamedQuery("Location.updateLatLong");
        geocodeSessionBean encoder = new geocodeSessionBean();

        try {
            List<persistence.Location> locationList = (List<persistence.Location>) locationQuery.getResultList();
            for (persistence.Location loc : locationList) {
                encoder.encodeAddress(loc);
    System.err.println(((
            (loc.getStreet()!=null || loc.getCity()!=null || loc.getState()!=null || loc.getZip()!=null) &&
            (loc.getLatitude() == null || loc.getLongitude()==null))?"ERROR ":"")+
            "Encoding '"+loc.getStreet()+"','"+loc.getCity()+"','"+loc.getState()+"','"+loc.getZip()+"' to "+loc.getLatitude()+", "+loc.getLongitude());
                if (loc.getLatitude() != null && loc.getLongitude()!=null) {
                    em.merge(loc); // ??
                    em.flush();
//                    updateQuery.setParameter("lat", loc.getLatitude());
//                    updateQuery.setParameter("lon", loc.getLongitude());
//                    updateQuery.executeUpdate();
                }
            }
        } catch (NoResultException nr) {
            System.out.println("No locations need updating");
            return;
        }
	}

	public void persist(Object object) {
		em.persist(object);
	}

	// Add business logic below. (Right-click in editor and choose
	// "Insert Code > Add Business Method" or "Web Service > Add Operation")

}